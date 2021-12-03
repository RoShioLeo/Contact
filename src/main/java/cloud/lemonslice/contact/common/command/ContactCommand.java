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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static cloud.lemonslice.contact.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;

public class ContactCommand
{
    private static final SuggestionProvider<CommandSource> SUGGEST_POSTCARDS = (context, builder) ->
    {
        Collection<ResourceLocation> collection = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet();
        return ISuggestionProvider.suggestResource(collection.stream(), builder);
    };

    public static void register(CommandDispatcher<CommandSource> dispatcher)
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

    private static int deliverPostcard(CommandSource source, ResourceLocation id, Collection<ServerPlayerEntity> targets, int ticks, String sender, String text)
    {
        text = text.replace("\\n", "\n");
        AtomicInteger n = new AtomicInteger(0);
        for (ServerPlayerEntity player : targets)
        {
            ItemStack postcard = PostcardItem.setText(PostcardItem.getPostcard(id, false), text);
            postcard.getOrCreateTag().putString("Sender", sender);

            player.getServer().getLevel(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
            {
                if (!data.PLAYERS_DATA.isMailboxFull(player.getUUID()))
                {
                    data.PLAYERS_DATA.mailList.add(new MailToBeSent(player.getUUID(), postcard, ticks));
                    n.getAndIncrement();
                }
                else
                {
                    source.sendSuccess(new TranslationTextComponent("command.contact.deliver.full", player.getDisplayName()), true);
                }
            });
        }

        if (targets.size() == 1 && n.get() == 1)
        {
            source.sendSuccess(new TranslationTextComponent("command.contact.deliver.success.single", new ItemStack(ItemRegistry.POSTCARD).getDisplayName(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendSuccess(new TranslationTextComponent("command.contact.deliver.success.multiple", new ItemStack(ItemRegistry.POSTCARD).getDisplayName(), n.get()), true);
        }
        return n.get();
    }

    private static int givePostcard(CommandSource source, ResourceLocation id, Collection<ServerPlayerEntity> targets, String sender, String text)
    {
        text = text.replace("\\n", "\n");
        for (ServerPlayerEntity serverplayerentity : targets)
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
            boolean flag = serverplayerentity.inventory.add(postcard);
            if (flag && postcard.isEmpty())
            {
                postcard.setCount(1);
                ItemEntity itemEntity = serverplayerentity.drop(postcard, false);
                if (itemEntity != null)
                {
                    itemEntity.makeFakeItem();
                }

                serverplayerentity.level.playSound(null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                serverplayerentity.inventoryMenu.broadcastChanges();
            }
            else
            {
                ItemEntity itementity = serverplayerentity.drop(postcard, false);
                if (itementity != null)
                {
                    itementity.setNoPickUpDelay();
                    itementity.setOwner(serverplayerentity.getUUID());
                }
            }
        }

        if (targets.size() == 1)
        {
            source.sendSuccess(new TranslationTextComponent("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD).getDisplayName(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendSuccess(new TranslationTextComponent("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD).getDisplayName(), targets.size()), true);
        }

        return targets.size();
    }
}
