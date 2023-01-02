package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.resourse.PostcardHandler;
import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.network.IToClientMessage;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

import static cloud.lemonslice.contact.Contact.MODID;

public class PostcardReloadMessage implements IToClientMessage
{
    private Map<Identifier, PostcardStyle> postcards;

    PostcardReloadMessage()
    {
        postcards = PostcardHandler.POSTCARD_MANAGER.getPostcards();
    }

    PostcardReloadMessage(Map<Identifier, PostcardStyle> postcards)
    {
        this.postcards = postcards;
    }

    @Override
    public PacketByteBuf toBytes()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(PostcardHandler.POSTCARD_MANAGER.getPostcards().size());
        PostcardHandler.POSTCARD_MANAGER.getPostcards().forEach((id, style) ->
        {
            buf.writeIdentifier(id);
            PostcardHandler.write(buf, style);
        });
        return buf;
    }

    @Override
    public void process(MinecraftClient client, ClientPlayNetworkHandler handler, PacketSender responseSender)
    {
        client.executeSync(() -> PostcardHandler.POSTCARD_MANAGER.getPostcardsFromServer(postcards));
    }

    public static Identifier getID()
    {
        return new Identifier(MODID, "postcard_reload");
    }

    public static PostcardReloadMessage fromBytes(PacketByteBuf buf)
    {
        int n = buf.readInt();
        ImmutableMap.Builder<Identifier, PostcardStyle> map = ImmutableMap.builder();
        for (int i = 0; i < n; i++)
        {
            Identifier id = buf.readIdentifier();
            PostcardStyle style = PostcardHandler.read(buf);
            map.put(id, style);
        }
        return new PostcardReloadMessage(map.build());
    }

    public static PostcardReloadMessage create()
    {
        return new PostcardReloadMessage();
    }
}
