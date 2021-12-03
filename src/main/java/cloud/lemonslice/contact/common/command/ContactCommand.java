package cloud.lemonslice.contact.common.command;

import cloud.lemonslice.contact.common.capability.MailToBeSent;
import cloud.lemonslice.contact.common.command.arguments.PostcardStyleArgument;
import cloud.lemonslice.contact.common.config.ServerConfig;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;

public class ContactCommand
{
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_POSTCARDS = (context, builder) ->
    {
        Collection<ResourceLocation> collection = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet();
        return SharedSuggestionProvider.suggestResource(collection.stream(), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("contact")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.literal("give")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("postcard", ResourceLocationArgument.id())
                                                        .suggests(SUGGEST_POSTCARDS)
                                                        .executes(context -> givePostcard(context.getSource(), PostcardStyleArgument.getPostcardStyleID(context, "postcard"), EntityArgument.getPlayers(context, "targets"), "", ""))
                                                        .then(Commands.argument("sender", StringArgumentType.word())
                                                                .executes(context -> givePostcard(context.getSource(), PostcardStyleArgument.getPostcardStyleID(context, "postcard"), EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "sender"), StringArgumentType.getString(context, "text")))
                                                                .then(Commands.argument("text", StringArgumentType.greedyString())
                                                                        .executes(context -> givePostcard(context.getSource(), PostcardStyleArgument.getPostcardStyleID(context, "postcard"), EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "sender"), StringArgumentType.getString(context, "text")))
                                                                )
                                                        )
                                                )
                                        )
                        )
                        .then(
                                Commands.literal("deliver")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("postcard", ResourceLocationArgument.id())
                                                        .suggests(SUGGEST_POSTCARDS)
                                                        .then(Commands.argument("ticks", IntegerArgumentType.integer(0, ServerConfig.Mail.postalSpeed.get() * 9000))
                                                                .then(Commands.argument("sender", StringArgumentType.word())
                                                                        .executes(context -> deliverPostcard(context.getSource(), PostcardStyleArgument.getPostcardStyleID(context, "postcard"), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "ticks"), StringArgumentType.getString(context, "sender"), ""))
                                                                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                                                                .executes(context -> deliverPostcard(context.getSource(), PostcardStyleArgument.getPostcardStyleID(context, "postcard"), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "ticks"), StringArgumentType.getString(context, "sender"), StringArgumentType.getString(context, "text")))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                        )
        );
    }

    private static int deliverPostcard(CommandSourceStack source, ResourceLocation id, Collection<ServerPlayer> targets, int ticks, String sender, String text)
    {
        text = text.replace("\\n", "\n");
        AtomicInteger n = new AtomicInteger(0);
        for (ServerPlayer player : targets)
        {
            ItemStack postcard = PostcardItem.setText(PostcardItem.getPostcard(id, false), text);
            postcard.getOrCreateTag().putString("Sender", sender);

            player.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
            {
                if (!data.getData().isMailboxFull(player.getUUID()))
                {
                    data.getData().mailList.add(new MailToBeSent(player.getUUID(), postcard, ticks));
                    n.getAndIncrement();
                }
                else
                {
                    source.sendSuccess(new TranslatableComponent("command.contact.deliver.full", player.getDisplayName()), true);
                }
            });
        }

        if (targets.size() == 1 && n.get() == 1)
        {
            source.sendSuccess(new TranslatableComponent("command.contact.deliver.success.single", new ItemStack(ItemRegistry.POSTCARD.get()).getDisplayName(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendSuccess(new TranslatableComponent("command.contact.deliver.success.multiple", new ItemStack(ItemRegistry.POSTCARD.get()).getDisplayName(), n.get()), true);
        }
        return n.get();
    }

    private static int givePostcard(CommandSourceStack source, ResourceLocation id, Collection<ServerPlayer> targets, String sender, String text)
    {
        text = text.replace("\\n", "\n");
        for (ServerPlayer serverplayer : targets)
        {
            ItemStack postcard;
            if (sender.isEmpty())
            {
                postcard = PostcardItem.getPostcard(id, false);
            }
            else
            {
                postcard = PostcardItem.setText(PostcardItem.getPostcard(id, false), text);
                postcard.getOrCreateTag().putString("Sender", sender);
            }
            boolean flag = serverplayer.getInventory().add(postcard);
            if (flag && postcard.isEmpty())
            {
                postcard.setCount(1);
                ItemEntity itemEntity = serverplayer.drop(postcard, false);
                if (itemEntity != null)
                {
                    itemEntity.makeFakeItem();
                }

                serverplayer.level.playSound(null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayer.getRandom().nextFloat() - serverplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                serverplayer.inventoryMenu.broadcastChanges();
            }
            else
            {
                ItemEntity itementity = serverplayer.drop(postcard, false);
                if (itementity != null)
                {
                    itementity.setNoPickUpDelay();
                    itementity.setOwner(serverplayer.getUUID());
                }
            }
        }

        if (targets.size() == 1)
        {
            source.sendSuccess(new TranslatableComponent("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD.get()).getDisplayName(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendSuccess(new TranslatableComponent("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD.get()).getDisplayName(), targets.size()), true);
        }

        return targets.size();
    }
}
