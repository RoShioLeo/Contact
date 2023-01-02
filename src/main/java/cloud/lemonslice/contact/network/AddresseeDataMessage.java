package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.screenhandler.PostboxScreenHandler;
import cloud.lemonslice.silveroak.network.IToClientMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.Contact.MODID;

public class AddresseeDataMessage implements IToClientMessage
{
    private final String name;
    private final int ticks; // Usually non-negative for send-ready, -1 for not-found, -2 for full-mailbox

    public AddresseeDataMessage(String name, int ticks)
    {
        this.name = name;
        this.ticks = ticks;
    }

    @Override
    public PacketByteBuf toBytes()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(name, 32767);
        buf.writeInt(ticks);
        return buf;
    }

    @Override
    public void process(MinecraftClient client, ClientPlayNetworkHandler handler, PacketSender responseSender)
    {
        client.executeSync(() ->
        {
            if (client.player.currentScreenHandler instanceof PostboxScreenHandler container)
            {
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

    public static Identifier getID()
    {
        return new Identifier(MODID, "addressee_data");
    }

    public static AddresseeDataMessage fromBytes(PacketByteBuf buf)
    {
        String name = buf.readString(32767);
        int ticks = buf.readInt();
        return create(name, ticks);
    }

    public static AddresseeDataMessage create(String name, int ticks)
    {
        return new AddresseeDataMessage(name, ticks);
    }
}
