package cloud.lemonslice.contact.common.command;

import cloud.lemonslice.contact.common.command.arguments.PostcardStyleArgument;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.mojang.brigadier.CommandDispatcher;
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

import java.util.Collection;

public class ContactCommand
{
    private static final SuggestionProvider<CommandSource> SUGGEST_POSTCARDS = (context, builder) ->
    {
        Collection<ResourceLocation> collection = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet();
        return ISuggestionProvider.func_212476_a(collection.stream(), builder);
    };

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
                Commands.literal("contact")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(
                                Commands.literal("deliver")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("postcard", ResourceLocationArgument.resourceLocation())
                                                        .suggests(SUGGEST_POSTCARDS)
                                                        .executes(context -> givePostcard(context.getSource(), PostcardStyleArgument.getPostcardStyleID(context, "postcard"), EntityArgument.getPlayers(context, "targets"), "", ""))
                                                        .then(Commands.argument("sender", StringArgumentType.word())
                                                                .then(Commands.argument("text", StringArgumentType.greedyString())
                                                                        .executes(context -> givePostcard(context.getSource(), PostcardStyleArgument.getPostcardStyleID(context, "postcard"), EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "sender"), StringArgumentType.getString(context, "text")))
                                                                )
                                                        )
                                                )
                                        )
                        )
        );
    }

    private static int givePostcard(CommandSource source, ResourceLocation id, Collection<ServerPlayerEntity> targets, String sender, String text)
    {
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
            boolean flag = serverplayerentity.inventory.addItemStackToInventory(postcard);
            if (flag && postcard.isEmpty())
            {
                postcard.setCount(1);
                ItemEntity itemEntity = serverplayerentity.dropItem(postcard, false);
                if (itemEntity != null)
                {
                    itemEntity.makeFakeItem();
                }

                serverplayerentity.world.playSound(null, serverplayerentity.getPosX(), serverplayerentity.getPosY(), serverplayerentity.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverplayerentity.getRNG().nextFloat() - serverplayerentity.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                serverplayerentity.container.detectAndSendChanges();
            }
            else
            {
                ItemEntity itementity = serverplayerentity.dropItem(postcard, false);
                if (itementity != null)
                {
                    itementity.setNoPickupDelay();
                    itementity.setOwnerId(serverplayerentity.getUniqueID());
                }
            }
        }

        if (targets.size() == 1)
        {
            source.sendFeedback(new TranslationTextComponent("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD).getTextComponent(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendFeedback(new TranslationTextComponent("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD).getTextComponent(), targets.size()), true);
        }

        return targets.size();
    }
}
