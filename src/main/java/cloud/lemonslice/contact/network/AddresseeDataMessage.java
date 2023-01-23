package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.screenhandler.PostboxScreenHandler;
import cloud.lemonslice.silveroak.network.IToClientMessage;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

import static cloud.lemonslice.contact.Contact.MODID;

public class AddresseeDataMessage implements IToClientMessage
{
    private final List<String> names;
    private final List<Integer> ticks; // -1 means mailbox is full, -2 means no mailbox

    AddresseeDataMessage(List<String> names, List<Integer> ticks)
    {
        this.names = names;
        this.ticks = ticks;
    }

    @Override
    public PacketByteBuf toBytes()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(names.size());
        for (String name : names)
        {
            buf.writeString(name, 32767);
        }
        for (int tick : ticks)
        {
            buf.writeInt(tick);
        }
        return buf;
    }

    @Override
    public void process(MinecraftClient client, ClientPlayNetworkHandler handler, PacketSender responseSender)
    {
        client.executeSync(() ->
        {
            if (client.player.currentScreenHandler instanceof PostboxScreenHandler container)
            {
                container.names = names;
                container.ticks = ticks;
            }
        });
    }

    public static Identifier getID()
    {
        return new Identifier(MODID, "addressee_data");
    }

    public static AddresseeDataMessage fromBytes(PacketByteBuf buf)
    {
        int size = buf.readInt();
        List<String> names = Lists.newArrayList();
        List<Integer> ticks = Lists.newArrayList();
        for (int i = 0; i < size; i++)
        {
            names.add(buf.readString(32767));
        }
        for (int i = 0; i < size; i++)
        {
            ticks.add(buf.readInt());
        }
        return create(names, ticks);
    }

    public static AddresseeDataMessage create(List<String> names, List<Integer> ticks)
    {
        return new AddresseeDataMessage(names, ticks);
    }
}
