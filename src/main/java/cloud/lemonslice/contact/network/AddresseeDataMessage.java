package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.common.container.PostboxContainer;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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

    public AddresseeDataMessage(FriendlyByteBuf buf)
    {
        this.name = buf.readUtf(32767);
        this.ticks = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer)
    {
        packetBuffer.writeUtf(name, 32767);
        packetBuffer.writeInt(ticks);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->
        {
            if (Contact.PROXY.getClientPlayer().containerMenu instanceof PostboxContainer)
            {
                PostboxContainer container = ((PostboxContainer) Contact.PROXY.getClientPlayer().containerMenu);
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
