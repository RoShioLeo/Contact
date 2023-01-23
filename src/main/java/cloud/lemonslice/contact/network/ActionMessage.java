package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.contact.common.screenhandler.PackageScreenHandler;
import cloud.lemonslice.contact.common.screenhandler.PostboxScreenHandler;
import cloud.lemonslice.contact.common.screenhandler.RedPacketEnvelopeScreenHandler;
import cloud.lemonslice.silveroak.network.IToClientMessage;
import cloud.lemonslice.silveroak.network.IToServerMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static cloud.lemonslice.contact.Contact.MODID;

public class ActionMessage implements IToClientMessage, IToServerMessage
{
    // From Server, 0 means new mail arrived.
    // From Client, 0 means player is packing a parcel. 1 means mail sent successfully.
    private final int action;
    private final String extra;

    ActionMessage(int action, String extra)
    {
        this.action = action;
        this.extra = extra;
    }

    @Override
    public PacketByteBuf toBytes()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(action);
        buf.writeString(Objects.requireNonNullElse(extra, ""), 32767);
        return buf;
    }

    @Override
    public void process(MinecraftClient client, ClientPlayNetworkHandler handler, PacketSender responseSender)
    {
        if (action == 0)
        {
            ClientProxy.notifyNewMail(client);
        }
        else if (action == 1)
        {
            if (client.player.currentScreenHandler instanceof PostboxScreenHandler container)
            {
                container.status = 2;
            }
        }
    }

    @Override
    public void process(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketSender responseSender)
    {
        if (player == null)
        {
            return;
        }

        if (action == 0)
        {
            packParcel(server, player, extra);
        }
    }

    public static Identifier getID()
    {
        return new Identifier(MODID, "action");
    }

    public static ActionMessage fromBytes(PacketByteBuf buf)
    {
        int action = buf.readInt();
        String extra = buf.readString(32767);
        return create(action, extra);
    }

    public static ActionMessage create(int action)
    {
        return new ActionMessage(action, null);
    }

    public static ActionMessage create(int action, String extra)
    {
        return new ActionMessage(action, extra);
    }

    private static void packParcel(MinecraftServer server, ServerPlayerEntity player, String extra)
    {
        server.executeSync(() ->
        {
            if (player.currentScreenHandler instanceof PackageScreenHandler screenHandler)
            {
                screenHandler.isPacked = true;
                if (screenHandler instanceof RedPacketEnvelopeScreenHandler redPacket)
                {
                    redPacket.blessings = extra;
                }
                player.closeHandledScreen();
            }
        });
    }
}
