package cloud.lemonslice.intercourse.network;

import cloud.lemonslice.intercourse.client.ClientProxy;
import cloud.lemonslice.intercourse.common.container.WrappingPaperContainer;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ActionMessage implements INormalMessage
{
    private final int action;

    public ActionMessage(int action)
    {
        this.action = action;
    }

    public ActionMessage(PacketBuffer buf)
    {
        this.action = buf.readInt();
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer)
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
            ServerPlayerEntity player = ctx.getSender();
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

    private static void packParcel(NetworkEvent.Context ctx, ServerPlayerEntity player)
    {
        ctx.enqueueWork(() ->
        {
            if (player.openContainer instanceof WrappingPaperContainer)
            {
                ((WrappingPaperContainer) player.openContainer).isPacked = true;
                player.closeScreen();
            }
        });
    }
}
