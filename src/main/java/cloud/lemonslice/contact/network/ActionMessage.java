package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.contact.common.container.WrappingPaperContainer;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ActionMessage implements INormalMessage
{
    private final int action;

    public ActionMessage(int action)
    {
        this.action = action;
    }

    public ActionMessage(FriendlyByteBuf buf)
    {
        this.action = buf.readInt();
    }

    private static void packParcel(NetworkEvent.Context ctx, ServerPlayer player)
    {
        ctx.enqueueWork(() ->
        {
            if (player.containerMenu instanceof WrappingPaperContainer)
            {
                ((WrappingPaperContainer) player.containerMenu).isPacked = true;
                player.closeContainer();
            }
        });
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer)
    {
        packetBuffer.writeInt(action);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            if (action == 0)
            {
                ClientProxy.notifyNewMail(ctx);
            }
        }
        else if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER)
        {
            ServerPlayer player = ctx.getSender();
            if (player == null)
            {
                return;
            }

            if (action == 0)
            {
                packParcel(ctx, player);
            }
        }
    }
}
