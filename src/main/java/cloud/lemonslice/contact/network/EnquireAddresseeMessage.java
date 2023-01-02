package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.handler.AdvancementManager;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.common.screenhandler.PostboxScreenHandler;
import cloud.lemonslice.contact.common.storage.MailToBeSent;
import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import cloud.lemonslice.silveroak.network.IToServerMessage;
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

import java.util.Locale;
import java.util.UUID;

import static cloud.lemonslice.contact.Contact.MODID;

public class EnquireAddresseeMessage implements IToServerMessage
{
    private final String nameIn;
    private final boolean shouldSend;

    public EnquireAddresseeMessage(String name, boolean shouldSend)
    {
        this.nameIn = name;
        this.shouldSend = shouldSend;
    }

    public EnquireAddresseeMessage(PacketByteBuf buf)
    {
        this.nameIn = buf.readString(32767);
        this.shouldSend = buf.readBoolean();
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
                ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(nameIn, -1).toBytes());
                return;
            }
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(server);
            String lowerIn = nameIn.toLowerCase(Locale.ROOT);
            if (lowerIn.equals("@e") && player.server.getPermissionLevel(player.getGameProfile()) >= 2)
            {
                if (shouldSend)
                {
                    if (player.currentScreenHandler instanceof PostboxScreenHandler)
                    {
                        PostboxScreenHandler container = ((PostboxScreenHandler) player.currentScreenHandler);
                        ItemStack parcel = container.parcel.getStack(0).copy();
                        parcel.getOrCreateNbt().putString("Sender", player.getName().getString());
                        for (UUID uuid : data.getData().nameToUUID.values())
                        {
                            data.getData().mailList.add(new MailToBeSent(uuid, parcel.copy(), 0));
                        }
                        ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(lowerIn, -3).toBytes());
                        container.parcel.setStack(0, ItemStack.EMPTY);
                    }
                }
                else
                {
                    ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(lowerIn, 0).toBytes());
                }
                return;
            }
            for (String name : data.getData().nameToUUID.keySet())
            {
                if (name.toLowerCase(Locale.ROOT).startsWith(lowerIn))
                {
                    UUID uuid = data.getData().nameToUUID.get(name);
                    if (data.getData().isMailboxFull(uuid))
                    {
                        ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(name, -2).toBytes());
                        return;
                    }
                    GlobalPos mailboxPos = data.getData().getMailboxPos(uuid);
                    if (player.currentScreenHandler instanceof PostboxScreenHandler)
                    {
                        int ticks = 0;
                        if (!((PostboxScreenHandler) player.currentScreenHandler).isEnderMail())
                        {
                            if (mailboxPos != null)
                            {
                                ticks = MailboxManager.getDeliveryTicks(player.getWorld().getRegistryKey(), player.getBlockPos(), mailboxPos.getDimension(), mailboxPos.getPos());
                            }
                            else
                            {
                                ticks = MailboxManager.getDeliveryTicks(player.getWorld().getRegistryKey(), player.getBlockPos(), World.OVERWORLD, player.getWorld().getSpawnPos());
                            }
                        }

                        if (shouldSend)
                        {
                            PostboxScreenHandler container = ((PostboxScreenHandler) player.currentScreenHandler);
                            ItemStack parcel = container.parcel.getStack(0);
                            parcel.getOrCreateNbt().putString("Sender", player.getName().getString());

                            if (parcel.getItem() instanceof PostcardItem)
                            {
                                AdvancementManager.givePlayerAdvancement(player.server, player, new Identifier("contact:send_postcard"));
                            }

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

                            data.getData().mailList.add(new MailToBeSent(uuid, parcel, ticks));
                            ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(name, -3).toBytes());
                            container.parcel.setStack(0, ItemStack.EMPTY);
                        }
                        else
                        {
                            ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(name, ticks).toBytes());
                        }
                        return;
                    }
                }
            }
            ServerPlayNetworking.send(player, AddresseeDataMessage.getID(), AddresseeDataMessage.create(nameIn, -1).toBytes());
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
