package cloud.lemonslice.intercourse.network;

import cloud.lemonslice.intercourse.Intercourse;
import cloud.lemonslice.intercourse.common.container.PostboxContainer;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AddresseeDataMessage implements INormalMessage
{
    private final String name;
    private final int ticks; // Usually non-negative for send-ready, -1 for not-found, -2 for full-mailbox

    public AddresseeDataMessage(String name, int ticks)
    {
        this.name = name;
        this.ticks = ticks;
    }

    public AddresseeDataMessage(PacketBuffer buf)
    {
        this.name = buf.readString();
        this.ticks = buf.readInt();
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer)
    {
        packetBuffer.writeString(name);
        packetBuffer.writeInt(ticks);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            ctx.enqueueWork(() ->
            {
                if (Intercourse.PROXY.getClientPlayer().openContainer instanceof PostboxContainer)
                {
                    PostboxContainer container = ((PostboxContainer) Intercourse.PROXY.getClientPlayer().openContainer);
                    container.playerName = name;
                    container.time = ticks;
                    if (ticks == -1)
                    {
                        container.status = 3;
                    }
                    else if (ticks == -2)
                    {
                        container.status = 4;
                    }
                    else if (ticks == -3)
                    {
                        container.status = 5;
                    }
                    else
                    {
                        container.status = 2;
                    }
                }
            });
        }
    }
}
