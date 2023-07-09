package cloud.lemonslice.contact.common.command;

import cloud.lemonslice.contact.common.command.arguments.PostcardStyleArgument;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.item.ParcelItem;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.common.storage.MailToBeSent;
import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import cloud.lemonslice.silveroak.SilveroakOutpost;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ContactCommand
{
    private static final SuggestionProvider<ServerCommandSource> SUGGEST_PLAYERS = (context, builder) ->
    {
        MinecraftServer server = SilveroakOutpost.getCurrentServer();
        Set<String> set = Sets.newHashSet();
        if (server != null)
        {
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(server);
            set.addAll(data.getData().nameToUUID.keySet());
        }
        set.add("\"@e\"");
        return CommandSource.suggestMatching(set, builder);
    };
    private static final SuggestionProvider<ServerCommandSource> SUGGEST_POSTCARDS = (context, builder) ->
    {
        Set<Identifier> collection = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet();
        return CommandSource.suggestIdentifiers(collection, builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(
                CommandManager.literal("contact")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("postcard")
                                .then(CommandManager.literal("give")
                                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                .then(CommandManager.argument("postcard", IdentifierArgumentType.identifier())
                                                        .suggests(SUGGEST_POSTCARDS)
                                                        .then(CommandManager.argument("isEnderType", BoolArgumentType.bool())
                                                                .executes(context -> givePostcard(context.getSource(),
                                                                        PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                        "",
                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                        "")
                                                                )
                                                                .then(CommandManager.argument("sender", StringArgumentType.string())
                                                                        .executes(context -> givePostcard(context.getSource(),
                                                                                PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                                EntityArgumentType.getPlayers(context, "targets"),
                                                                                StringArgumentType.getString(context, "sender"),
                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                StringArgumentType.getString(context, "text"))
                                                                        )
                                                                        .then(CommandManager.argument("text", StringArgumentType.greedyString())
                                                                                .executes(context -> givePostcard(context.getSource(),
                                                                                        PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                                        EntityArgumentType.getPlayers(context, "targets"),
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
                                .then(CommandManager.literal("deliver")
                                        .then(CommandManager.argument("targets", StringArgumentType.string())
                                                .suggests(SUGGEST_PLAYERS)
                                                .then(CommandManager.argument("postcard", IdentifierArgumentType.identifier())
                                                        .suggests(SUGGEST_POSTCARDS)
                                                        .then(CommandManager.argument("isEnderType", BoolArgumentType.bool())
                                                                .then(CommandManager.argument("sender", StringArgumentType.string())
                                                                        .then(CommandManager.argument("ticks", IntegerArgumentType.integer(0, 1728000))
                                                                                .executes(context -> deliverPostcard(context.getSource(),
                                                                                        PostcardStyleArgument.getPostcardStyleID(context, "postcard"),
                                                                                        StringArgumentType.getString(context, "targets"),
                                                                                        IntegerArgumentType.getInteger(context, "ticks"),
                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                        "")
                                                                                )
                                                                                .then(CommandManager.argument("text", StringArgumentType.greedyString())
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
                        .then(CommandManager.literal("parcel")
                                .then(CommandManager.literal("give")
                                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                .then(CommandManager.argument("isEnderType", BoolArgumentType.bool())
                                                        .then(CommandManager.argument("sender", StringArgumentType.string())
                                                                .then(CommandManager.argument("item1", ItemStackArgumentType.itemStack(access))
                                                                        .then(CommandManager.argument("count1", IntegerArgumentType.integer(1, 64))
                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false))
                                                                                )
                                                                                .then(CommandManager.argument("item2", ItemStackArgumentType.itemStack(access))
                                                                                        .then(CommandManager.argument("count2", IntegerArgumentType.integer(1, 64))
                                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item2").createStack(IntegerArgumentType.getInteger(context, "count2"), false))
                                                                                                )
                                                                                                .then(CommandManager.argument("item3", ItemStackArgumentType.itemStack(access))
                                                                                                        .then(CommandManager.argument("count3", IntegerArgumentType.integer(1, 64))
                                                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item2").createStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item3").createStack(IntegerArgumentType.getInteger(context, "count3"), false))
                                                                                                                )
                                                                                                                .then(CommandManager.argument("item4", ItemStackArgumentType.itemStack(access))
                                                                                                                        .then(CommandManager.argument("count4", IntegerArgumentType.integer(1, 64))
                                                                                                                                .executes(context -> giveParcel(context.getSource(),
                                                                                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                                                                                        StringArgumentType.getString(context, "sender"),
                                                                                                                                        BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item2").createStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item3").createStack(IntegerArgumentType.getInteger(context, "count3"), false),
                                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item4").createStack(IntegerArgumentType.getInteger(context, "count4"), false)))
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
                                .then(CommandManager.literal("deliver")
                                        .then(CommandManager.argument("targets", StringArgumentType.string())
                                                .suggests(SUGGEST_PLAYERS)
                                                .then(CommandManager.argument("isEnderType", BoolArgumentType.bool())
                                                        .then(CommandManager.argument("sender", StringArgumentType.string())
                                                                .then(CommandManager.argument("ticks", IntegerArgumentType.integer(0, 1728000))
                                                                        .then(CommandManager.argument("item1", ItemStackArgumentType.itemStack(access))
                                                                                .then(CommandManager.argument("count1", IntegerArgumentType.integer(1, 64))
                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false))
                                                                                        )
                                                                                        .then(CommandManager.argument("item2", ItemStackArgumentType.itemStack(access))
                                                                                                .then(CommandManager.argument("count2", IntegerArgumentType.integer(1, 64))
                                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item2").createStack(IntegerArgumentType.getInteger(context, "count2"), false))
                                                                                                        )
                                                                                                        .then(CommandManager.argument("item3", ItemStackArgumentType.itemStack(access))
                                                                                                                .then(CommandManager.argument("count3", IntegerArgumentType.integer(1, 64))
                                                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item2").createStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item3").createStack(IntegerArgumentType.getInteger(context, "count3"), false))
                                                                                                                        )
                                                                                                                        .then(CommandManager.argument("item4", ItemStackArgumentType.itemStack(access))
                                                                                                                                .then(CommandManager.argument("count4", IntegerArgumentType.integer(1, 64))
                                                                                                                                        .executes(context -> deliverParcel(context.getSource(),
                                                                                                                                                StringArgumentType.getString(context, "targets"),
                                                                                                                                                IntegerArgumentType.getInteger(context, "ticks"),
                                                                                                                                                StringArgumentType.getString(context, "sender"),
                                                                                                                                                BoolArgumentType.getBool(context, "isEnderType"),
                                                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item1").createStack(IntegerArgumentType.getInteger(context, "count1"), false),
                                                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item2").createStack(IntegerArgumentType.getInteger(context, "count2"), false),
                                                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item3").createStack(IntegerArgumentType.getInteger(context, "count3"), false),
                                                                                                                                                ItemStackArgumentType.getItemStackArgument(context, "item4").createStack(IntegerArgumentType.getInteger(context, "count4"), false)))
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

    private static void deliverToPlayerMailbox(ServerCommandSource source, String target, int ticks, AtomicInteger n, ItemStack parcel)
    {
        MailboxDataStorage data = MailboxDataStorage.getMailboxData(source.getServer());
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
                source.sendFeedback(() -> Text.translatable("command.contact.deliver.full", target), true);
            }
        }
    }

    private static int deliverParcel(ServerCommandSource source, String target, int ticks, String sender, boolean isEnder, ItemStack... list)
    {
        AtomicInteger n = new AtomicInteger(0);
        SimpleInventory contents = new SimpleInventory(4);
        for (int i = 0; i < list.length; i++)
        {
            contents.setStack(i, list[i]);
        }
        ItemStack parcel = ParcelItem.getParcel(contents, isEnder, sender);
        if (target.equals("@e"))
        {
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(source.getServer());
            data.getData().nameToUUID.keySet().forEach(name -> deliverToPlayerMailbox(source, name, ticks, n, parcel));
        }
        else
        {
            deliverToPlayerMailbox(source, target, ticks, n, parcel);
        }

        if (n.get() == 1 && target.equals("@e"))
        {
            source.sendFeedback(() -> Text.translatable("command.contact.deliver.success.single", new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL : ItemRegistry.PARCEL).getName(), target), true);
        }
        else
        {
            source.sendFeedback(() -> Text.translatable("command.contact.deliver.success.multiple", new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL : ItemRegistry.PARCEL).getName(), n.get()), true);
        }
        return n.get();
    }

    private static int giveParcel(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String sender, boolean isEnder, ItemStack... list)
    {
        SimpleInventory contents = new SimpleInventory(4);
        for (int i = 0; i < list.length; i++)
        {
            contents.setStack(i, list[i]);
        }
        ItemStack parcel = ParcelItem.getParcel(contents, isEnder, sender);

        giveParcelToPlayers(targets, parcel);

        if (targets.size() == 1)
        {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", 1, new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL : ItemRegistry.PARCEL).getName(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", 1, new ItemStack(isEnder ? ItemRegistry.ENDER_PARCEL : ItemRegistry.PARCEL).getName(), targets.size()), true);
        }

        return targets.size();
    }

    private static int deliverPostcard(ServerCommandSource source, Identifier id, String target, int ticks, String sender, boolean isEnder, String text)
    {
        text = text.replace("\\n", "\n");
        AtomicInteger n = new AtomicInteger(0);
        if (target.equals("@e"))
        {
            ItemStack postcard = PostcardItem.setText(PostcardItem.getPostcard(id, isEnder), text);
            postcard.getOrCreateNbt().putString("Sender", sender);
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(source.getServer());
            data.getData().nameToUUID.keySet().forEach(name -> deliverToPlayerMailbox(source, name, ticks, n, postcard));
        }
        else
        {
            ItemStack postcard = PostcardItem.setText(PostcardItem.getPostcard(id, false), text);
            postcard.getOrCreateNbt().putString("Sender", sender);

            deliverToPlayerMailbox(source, target, ticks, n, postcard);
        }

        if (n.get() == 1 && !target.equals("@e"))
        {
            source.sendFeedback(() -> Text.translatable("command.contact.deliver.success.single", new ItemStack(isEnder ? ItemRegistry.ENDER_POSTCARD : ItemRegistry.POSTCARD).getName(), target), true);
        }
        else
        {
            source.sendFeedback(() -> Text.translatable("command.contact.deliver.success.multiple", new ItemStack(isEnder ? ItemRegistry.ENDER_POSTCARD : ItemRegistry.POSTCARD).getName(), n.get()), true);
        }
        return n.get();
    }

    private static int givePostcard(ServerCommandSource source, Identifier id, Collection<ServerPlayerEntity> targets, String sender, boolean isEnder, String text)
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
            postcard.getOrCreateNbt().putString("Sender", sender);
        }
        giveParcelToPlayers(targets, postcard);

        if (targets.size() == 1)
        {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD).getName(), targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", 1, new ItemStack(ItemRegistry.POSTCARD).getName(), targets.size()), true);
        }

        return targets.size();
    }

    private static void giveParcelToPlayers(Collection<ServerPlayerEntity> targets, ItemStack parcel)
    {
        for (ServerPlayerEntity serverPlayer : targets)
        {
            boolean flag = serverPlayer.getInventory().insertStack(parcel);
            if (flag)
            {
                ItemEntity itemEntity = serverPlayer.dropItem(parcel, false);
                if (itemEntity != null)
                {
                    itemEntity.setDespawnImmediately();
                }

                serverPlayer.getWorld().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                serverPlayer.playerScreenHandler.sendContentUpdates();
            }
            else
            {
                ItemEntity itementity = serverPlayer.dropItem(parcel, false);
                if (itementity != null)
                {
                    itementity.resetPickupDelay();
                    itementity.setOwner(serverPlayer.getUuid());
                }
            }
        }
    }
}
