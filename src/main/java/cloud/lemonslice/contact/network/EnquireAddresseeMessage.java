package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.handler.AdvancementManager;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.item.IPackageItem;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.common.screenhandler.PostboxScreenHandler;
import cloud.lemonslice.contact.common.storage.MailToBeSent;
import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import cloud.lemonslice.silveroak.network.IToServerMessage;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static cloud.lemonslice.contact.Contact.MODID;

public class EnquireAddresseeMessage implements IToServerMessage
{
    private final String nameIn;
    private final boolean shouldSend;

    EnquireAddresseeMessage(String name, boolean shouldSend)
    {
        this.nameIn = name;
        this.shouldSend = shouldSend;
    }

    @Override
    public PacketByteBuf toBytes()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(nameIn, 32767);
        buf.writeBoolean(shouldSend);
        return buf;
    }

    @Override
    public void process(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketSender responseSender)
    {
        if (player == null)
        {
            return;
        }

        server.executeSync(() ->
        {
            if (nameIn.isEmpty())
            {
                return;
            }
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(server);
            String lowerIn = nameIn.toLowerCase(Locale.ROOT);
            if (lowerIn.equals("@e") && player.server.getPermissionLevel(player.getGameProfile()) >= 2) // 管理员全服寄送
            {
                if (shouldSend)
                {
                    if (player.currentScreenHandler instanceof PostboxScreenHandler container)
                    {
                        ItemStack parcel = container.parcel.getStack(0).copy();
                        parcel.getOrCreateNbt().putString("Sender", player.getName().getString());
                        for (UUID uuid : data.getData().nameToUUID.values())
                        {
                            data.getData().mailList.add(new MailToBeSent(uuid, parcel.copy(), 0));
                        }
                        ServerPlayNetworking.send(player, ActionMessage.getID(), ActionMessage.create(1).toBytes());
                        container.parcel.setStack(0, ItemStack.EMPTY);
                    }
                }
                else
                {
                    ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(Lists.newArrayList("@e"), Lists.newArrayList(0)).toBytes());
                }
                return;
            }
            List<String> names = Lists.newArrayList();
            for (String name : data.getData().nameToUUID.keySet())  //  查找符合前缀的用户
            {
                if (name.toLowerCase(Locale.ROOT).startsWith(lowerIn))
                {
                    names.add(name);
                }
                if (names.size() == 4)
                {
                    break;
                }
            }
            List<Integer> ticks = Lists.newArrayList();
            for (String name : names)
            {
                UUID uuid = data.getData().nameToUUID.get(name);
                if (data.getData().isMailboxFull(uuid))
                {
                    ticks.add(-1);
                    continue;
                }
                GlobalPos mailboxPos = data.getData().getMailboxPos(uuid);
                if (player.currentScreenHandler instanceof PostboxScreenHandler)
                {
                    int tick = 0;
                    if (!((PostboxScreenHandler) player.currentScreenHandler).isEnderMail())
                    {
                        if (mailboxPos != null)
                        {
                            tick = MailboxManager.getDeliveryTicks(player.getWorld().getRegistryKey(), player.getBlockPos(), mailboxPos.getDimension(), mailboxPos.getPos());
                        }
                        else
                        {
                            tick = ContactConfig.enableCenterMailbox ? MailboxManager.getDeliveryTicks(player.getWorld().getRegistryKey(), player.getBlockPos(), World.OVERWORLD, player.getWorld().getSpawnPos()) : -2;
                        }
                    }
                    ticks.add(tick);
                }
            }
            if (player.currentScreenHandler instanceof PostboxScreenHandler)
            {
                if (shouldSend && !names.isEmpty() && Objects.equals(names.get(0), nameIn) && ticks.get(0) >= 0)
                {
                    PostboxScreenHandler container = ((PostboxScreenHandler) player.currentScreenHandler);
                    ItemStack parcel = container.parcel.getStack(0);
                    parcel.getOrCreateNbt().putString("Sender", player.getName().getString());

                    if (IPackageItem.checkAndPostmarkPostcard(parcel, player.getName().getString()) || parcel.getItem() instanceof PostcardItem)
                    {
                        AdvancementManager.givePlayerAdvancement(player.server, player, new Identifier("contact:send_postcard"));
                    }

                    UUID uuid = data.getData().nameToUUID.get(names.get(0));
                    GlobalPos mailboxPos = data.getData().getMailboxPos(uuid);
                    if (mailboxPos != null)
                    {
                        if (mailboxPos.getDimension() != player.getWorld().getRegistryKey())
                        {
                            parcel.getOrCreateNbt().putBoolean("AnotherWorld", true);
                        }
                    }
                    else
                    {
                        if (World.OVERWORLD != player.getWorld().getRegistryKey())
                        {
                            parcel.getOrCreateNbt().putBoolean("AnotherWorld", true);
                        }
                    }

                    data.getData().mailList.add(new MailToBeSent(uuid, parcel, ticks.get(0)));
                    ServerPlayNetworking.send(player, ActionMessage.getID(), ActionMessage.create(1).toBytes());
                    container.parcel.setStack(0, ItemStack.EMPTY);
                }
                else
                {
                    ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(names, ticks).toBytes());
                }
            }
        });
    }

    public static Identifier getID()
    {
        return new Identifier(MODID, "enquire_addressee");
    }

    public static EnquireAddresseeMessage fromBytes(PacketByteBuf buf)
    {
        String name = buf.readString(32767);
        boolean shouldSend = buf.readBoolean();
        return create(name, shouldSend);
    }

    public static EnquireAddresseeMessage create(String name, boolean shouldSend)
    {
        return new EnquireAddresseeMessage(name, shouldSend);
    }
}
