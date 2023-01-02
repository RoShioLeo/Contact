package cloud.lemonslice.contact.resourse;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.network.PostcardReloadMessage;
import cloud.lemonslice.silveroak.SilveroakOutpost;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public final class PostcardManager extends JsonDataLoader implements IdentifiableResourceReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private Map<Identifier, PostcardStyle> postcards = ImmutableMap.of();

    public PostcardManager()
    {
        super(GSON, "postcards");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> objectIn, ResourceManager resourceManagerIn, Profiler profilerIn)
    {
        ImmutableMap.Builder<Identifier, PostcardStyle> map = ImmutableMap.builder();

        for (Map.Entry<Identifier, JsonElement> entry : objectIn.entrySet())
        {
            Identifier location = entry.getKey();
            if (location.getPath().startsWith("_")) continue;

            try
            {
                PostcardStyle style = PostcardHandler.read(JsonHelper.asObject(entry.getValue(), "top element"));
                map.put(location, style);
            }
            catch (IllegalArgumentException | JsonParseException exception)
            {
                Contact.error("Parsing error loading postcard style %s, throws %s", location, exception);
            }
        }

        this.postcards = map.build();
        Contact.info("Loaded %s postcard styles", postcards.size());

        MinecraftServer server = SilveroakOutpost.getCurrentServer();
        if (server != null)
        {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
            {
                ServerPlayNetworking.send(player, PostcardReloadMessage.getID(), PostcardReloadMessage.create().toBytes());
            }
        }
    }

    public void getPostcardsFromServer(Map<Identifier, PostcardStyle> postcards)
    {
        this.postcards = postcards;
        Contact.info("Loaded %d postcard styles from server", postcards.size());
    }

    public Map<Identifier, PostcardStyle> getPostcards()
    {
        return postcards;
    }

    public PostcardStyle getPostcard(Identifier id)
    {
        return postcards.getOrDefault(id, PostcardStyle.DEFAULT);
    }

    @Override
    public Identifier getFabricId()
    {
        return Contact.getIdentifier("postcard");
    }
}
