package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.resourse.PostcardHandler;
import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.network.INormalMessage;
import com.google.common.collect.ImmutableMap;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class PostcardReloadMessage implements INormalMessage
{
    private Map<ResourceLocation, PostcardStyle> postcards;

    public PostcardReloadMessage()
    {
        postcards = PostcardHandler.POSTCARD_MANAGER.getPostcards();
    }

    public PostcardReloadMessage(PacketBuffer buf)
    {
        int n = buf.readInt();
        ImmutableMap.Builder<ResourceLocation, PostcardStyle> map = ImmutableMap.builder();
        for (int i = 0; i < n; i++)
        {
            ResourceLocation id = buf.readResourceLocation();
            PostcardStyle style = PostcardHandler.read(buf);
            map.put(id, style);
        }
        postcards = map.build();
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer)
    {
        packetBuffer.writeInt(PostcardHandler.POSTCARD_MANAGER.getPostcards().size());
        PostcardHandler.POSTCARD_MANAGER.getPostcards().forEach((id, style) ->
        {
            packetBuffer.writeResourceLocation(id);
            PostcardHandler.write(packetBuffer, style);
        });
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> PostcardHandler.POSTCARD_MANAGER.setPostcards(postcards));
    }
}
