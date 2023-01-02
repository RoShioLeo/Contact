package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.contact.common.screenhandler.WrappingPaperScreenHandler;
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

import static cloud.lemonslice.contact.Contact.MODID;

public class ActionMessage implements IToClientMessage, IToServerMessage
{
    private final int action;  // action = 0. From Server, means new mail arrived. From Client, means player is packing a parcel.

    public ActionMessage(int action)
    {
        this.action = action;
    }

    @Override
    public PacketByteBuf toBytes()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(action);
        return buf;
    }

    @Override
    public void process(MinecraftClient client, ClientPlayNetworkHandler handler, PacketSender responseSender)
    {
        if (action == 0)
        {
            ClientProxy.notifyNewMail(client);
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
            packParcel(server, player);
        }
    }

    public static Identifier getID()
    {
        return new Identifier(MODID, "action");
    }

    public static ActionMessage fromBytes(PacketByteBuf buf)
    {
        int action = buf.readInt();
        return create(action);
    }

    public static ActionMessage create(int action)
    {
        return new ActionMessage(action);
    }

    private static void packParcel(MinecraftServer server, ServerPlayerEntity player)
    {
        server.executeSync(() ->
        {
            if (player.currentScreenHandler instanceof WrappingPaperScreenHandler screenHandler)
            {
                screenHandler.isPacked = true;
                player.closeHandledScreen();
            }
        });
    }
}
