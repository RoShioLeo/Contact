package cloud.lemonslice.contact.common.command;

import cloud.lemonslice.contact.common.capability.MailToBeSent;
import cloud.lemonslice.contact.common.command.arguments.PostcardStyleArgument;
import cloud.lemonslice.contact.common.config.ServerConfig;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.item.ParcelItem;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;

public class ContactCommand
{
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_PLAYERS = (context, builder) ->
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Set<String> set = Sets.newHashSet();
        if (server != null)
        {
            set.addAll(server.getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).map(data ->
                    data.getData().nameToUUID.keySet()).orElse(Sets.newHashSet()));
        }
        set.add("\"@e\"");
        return SharedSuggestionProvider.suggest(set, builder);
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_POSTCARDS = (context, builder) ->
    {
        Set<ResourceLocation> collection = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet();
        return SharedSuggestionProvider.suggestResource(collection, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("contact")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("postcard")
                                .then(Commands.literal("give")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("postcard", ResourceLocationArgument.id())
                                                        .suggests(SUGGEST_POSTCARDS)
                                                        .then(Commands.argument("isEnderType", BoolArgumentType.bool())
                                                                .executes(context -> givePostcard(context.getSource(),
                                                                        PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                        "",
                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                        "")
                                                                )
                                                                .then(Commands.argument("sender", StringArgumentType.string())
                                                                        .executes(context -> givePostcard(context.getSource(),
                                                                                PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                                EntityArgument.getPlayers(context, "targets"),
                                                                                StringArgumentType.getString(context, "sender"),
                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                StringArgumentType.getString(context, "text"))
                                                                        )
                                                                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                                                                .executes(context -> givePostcard(context.getSource(),
                                                                                        PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                        StringArgumentType.getString(context, "text"))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("deliver")
                                        .then(Commands.argument("targets", StringArgumentType.string())
                                                .suggests(SUGGEST_PLAYERS)
                                                .then(Commands.argument("postcard", ResourceLocationArgument.id())
                                                        .suggests(SUGGEST_POSTCARDS)
                                                        .then(Commands.argument("isEnderType", BoolArgumentType.bool())
                                                                .then(Commands.argument("sender", StringArgumentType.string())
                                                                        .then(Commands.argument("ticks", IntegerArgumentType.integer(0, ServerConfig.Mail.postalSpeed.get() * 9000))
                                                                                .executes(context -> deliverPostcard(context.getSource(),
                                                                                        PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                                        StringArgumentType.getString(context, "targets"),
                                                                                        IntegerArgumentType.getInteger(context, "ticks"),
                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                        "")
                                                                                )
                                                                                .then(Commands.argument("text", StringArgumentType.greedyString())
                                                                                        .executes(context -> deliverPostcard(context.getSource(),
                                                                                                PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                StringArgumentType.getString(context, "text"))
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("parcel")
                                .then(Commands.literal("give")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("isEnderType", BoolArgumentType.bool())
                                                        .then(Commands.argument("sender", StringArgumentType.string())
                                                                .then(Commands.argument("item1", ItemArgument.item())
                                                                        .then(Commands.argument("count1", IntegerArgumentType.integer(1, 64))
                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                        ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false))
                                                                                )
                                                                                .then(Commands.argument("item2", ItemArgument.item())
                                                                                        .then(Commands.argument("count2", IntegerArgumentType.integer(1, 64))
                                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                        ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                        ItemArgument.getItem(context, "item2").createItemStack(IntegerArgumentType.getInteger(context, "count2"), false))
                                                                                                )
                                                                                                .then(Commands.argument("item3", ItemArgument.item())
                                                                                                        .then(Commands.argument("count3", IntegerArgumentType.integer(1, 64))
                                                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                        ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                        ItemArgument.getItem(context, "item2").createItemStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                        ItemArgument.getItem(context, "item3").createItemStack(IntegerArgumentType.getInteger(context, "count3"), false))
                                                                                                                )
                                                                                                                .then(Commands.argument("item4", ItemArgument.item())
                                                                                                                        .then(Commands.argument("count4", IntegerArgumentType.integer(1, 64))
                                                                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                                                                        EntityArgument.getPlayers(context, "targets"),
                                                                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                                        ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                                        ItemArgument.getItem(context, "item2").createItemStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                                        ItemArgument.getItem(context, "item3").createItemStack(IntegerArgumentType.getInteger(context, "count3"), false),
                                                                                                                                        ItemArgument.getItem(context, "item4").createItemStack(IntegerArgumentType.getInteger(context, "count4"), false)))
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("deliver")
                                        .then(Commands.argument("targets", StringArgumentType.string())
                                                .suggests(SUGGEST_PLAYERS)
                                                .then(Commands.argument("isEnderType", BoolArgumentType.bool())
                                                        .then(Commands.argument("sender", StringArgumentType.string())
                                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0, ServerConfig.Mail.postalSpeed.get() * 9000))
                                                                        .then(Commands.argument("item1", ItemArgument.item())
                                                                                .then(Commands.argument("count1", IntegerArgumentType.integer(1, 64))
                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false))
                                                                                        )
                                                                                        .then(Commands.argument("item2", ItemArgument.item())
                                                                                                .then(Commands.argument("count2", IntegerArgumentType.integer(1, 64))
                                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                ItemArgument.getItem(context, "item2").createItemStack(IntegerArgumentType.getInteger(context, "count2"), false))
                                                                                                        )
                                                                                                        .then(Commands.argument("item3", ItemArgument.item())
                                                                                                                .then(Commands.argument("count3", IntegerArgumentType.integer(1, 64))
                                                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                                ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                                ItemArgument.getItem(context, "item2").createItemStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                                ItemArgument.getItem(context, "item3").createItemStack(IntegerArgumentType.getInteger(context, "count3"), false))
                                                                                                                        )
                                                                                                                        .then(Commands.argument("item4", ItemArgument.item())
                                                                                                                                .then(Commands.argument("count4", IntegerArgumentType.integer(1, 64))
                                                                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                                                ItemArgument.getItem(context, "item1").createItemStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                                                ItemArgument.getItem(context, "item2").createItemStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                                                ItemArgument.getItem(context, "item3").createItemStack(IntegerArgumentType.getInteger(context, "count3"), false),
                                                                                                                                                ItemArgument.getItem(context, "item4").createItemStack(IntegerArgumentType.getInteger(context, "count4"), false)))
                                                                                                                                )
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static void deliverToPlayerMailbox(CommandSourceStack source, String target, int ticks, AtomicInteger n, ItemStack parcel)
    {
        source.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
        {
            UUID uuid = data.getData().nameToUUID.get(target);
            if (uuid != null)
            {
                if (!data.getData().isMailboxFull(uuid))
                {
                    data.getData().mailList.add(new MailToBeSent(uuid, parcel, ticks));
                    n.getAndIncrement();
                }
                else
                {
                    source.sendSuccess(new TranslatableComponent("command.contact.deliver.full", target), true);
                }
            }
        });
    }

    private static int deliverParcel(CommandSourceStack source, String target, int ticks, String sender, boolean isEnder, ItemStack... list)
    {
        AtomicInteger n = new AtomicInteger(0);
        ItemStackHandler contents = new ItemStackHandler(4);
        for (int i = 0; i < list.length; i++)
        {
            contents.setStackInSlot(i, list[i]);
        }
        ItemStack parcel = ParcelItem.getParcel(contents, isEnder, sender);
        if (target.equals("@e"))
        {
            source.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
                    data.getData().nameToUUID.keySet().forEach(name -> deliverToPlayerMailbox(source, name, ticks, n, parcel)));
        }
        else
        {
            deliverToPlayerMailbox(source, target, ticks, n, parcel);
        }

        if (n.get() == 1 && target.equals("@e"))
        {
            source.sendSuccess(new TranslatableComponent("command.contact.deliver.success.single", new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL.get() : ItemRegistry.PARCEL.get()).getDisplayName(), target), true);
        }
        else
        {
            source.sendSuccess(new TranslatableComponent("command.contact.deliver.success.multiple", new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL.get() : ItemRegistry.PARCEL.get()).getDisplayName(), n.get()), true);
        }
        return n.get();
    }

    private static int giveParcel(CommandSourceStack source, Collection<ServerPlayer> targets, String sender, boolean isEnder, ItemStack... list)
    {
        ItemStackHandler contents = new ItemStackHandler(4);
        for (int i = 0; i < list.length; i++)
        {
            contents.setStackInSlot(i, list[i]);
        }
        ItemStack parcel = ParcelItem.getParcel(contents, isEnder, sender);

        giveParcelToPlayers(targets, parcel);

        if (targets.size() == 1)
        {
            source.sendSuccess(new TranslatableComponent("commands.give.success.single", 1, new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL.get() : ItemRegistry.PARCEL.get()).getDisplayName(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendSuccess(new TranslatableComponent("commands.give.success.single", 1, new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL.get() : ItemRegistry.PARCEL.get()).getDisplayName(), targets.size()), true);
        }

        return targets.size();
    }

    private static int deliverPostcard(CommandSourceStack source, ResourceLocation id, String target, int ticks, String sender, boolean isEnder, String text)
    {
        text = text.replace("\\n", "\n");
        AtomicInteger n = new AtomicInteger(0);
        if (target.equals("@e"))
        {
            ItemStack postcard = PostcardItem.setText(PostcardItem.getPostcard(id, isEnder), text);
            postcard.getOrCreateTag().putString("Sender", sender);
            source.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
                    data.getData().nameToUUID.keySet().forEach(name -> deliverToPlayerMailbox(source, name, ticks, n, postcard)));
        }
        else
        {
            ItemStack postcard = PostcardItem.setText(PostcardItem.getPostcard(id, false), text);
            postcard.getOrCreateTag().putString("Sender", sender);

            deliverToPlayerMailbox(source, target, ticks, n, postcard);
        }

        if (n.get() == 1 && !target.equals("@e"))
        {
            source.sendSuccess(new TranslatableComponent("command.contact.deliver.success.single", new ItemStack(isEnder ? ItemRegistry.ENDER_POSTCARD.get() : ItemRegistry.POSTCARD.get()).getDisplayName(), target), true);
        }
        else
        {
            source.sendSuccess(new TranslatableComponent("command.contact.deliver.success.multiple", new ItemStack(isEnder ? ItemRegistry.ENDER_POSTCARD.get() : ItemRegistry.POSTCARD.get()).getDisplayName(), n.get()), true);
        }
        return n.get();
    }

    private static int givePostcard(CommandSourceStack source, ResourceLocation id, Collection<ServerPlayer> targets, String sender, boolean isEnder, String text)
    {
        text = text.replace("\\n", "\n");
        ItemStack postcard;
        if (sender.isEmpty())
        {
            postcard = PostcardItem.getPostcard(id, isEnder);
        }
        else
        {
            postcard = PostcardItem.setText(PostcardItem.getPostcard(id, isEnder), text);
            postcard.getOrCreateTag().putString("Sender", sender);
        }
        giveParcelToPlayers(targets, postcard);

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

    private static void giveParcelToPlayers(Collection<ServerPlayer> targets, ItemStack parcel)
    {
        for (ServerPlayer serverplayer : targets)
        {
            boolean flag = serverplayer.getInventory().add(parcel);
            if (flag)
            {
                ItemEntity itemEntity = serverplayer.drop(parcel, false);
                if (itemEntity != null)
                {
                    itemEntity.makeFakeItem();
                }

                serverplayer.level.playSound(null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayer.getRandom().nextFloat() - serverplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                serverplayer.inventoryMenu.broadcastChanges();
            }
            else
            {
                ItemEntity itementity = serverplayer.drop(parcel, false);
                if (itementity != null)
                {
                    itementity.setNoPickUpDelay();
                    itementity.setOwner(serverplayer.getUUID());
                }
            }
        }
    }
}
