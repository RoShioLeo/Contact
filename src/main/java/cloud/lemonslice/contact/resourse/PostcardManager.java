package cloud.lemonslice.contact.resourse;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.network.PostcardReloadMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Map;

public final class PostcardManager extends JsonReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private Map<ResourceLocation, PostcardStyle> postcards = ImmutableMap.of();

    public PostcardManager()
    {
        super(GSON, "postcards");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        ImmutableMap.Builder<ResourceLocation, PostcardStyle> map = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet())
        {
            ResourceLocation location = entry.getKey();
            if (location.getPath().startsWith("_")) continue;

            try
            {
                PostcardStyle style = PostcardHandler.read(JSONUtils.getJsonObject(entry.getValue(), "top element"));
                map.put(location, style);
            }
            catch (IllegalArgumentException | JsonParseException exception)
            {
                Contact.error("Parsing error loading postcard style %s, throws %s", location, exception);
            }
        }

        this.postcards = map.build();
        Contact.info("Loaded %s postcard styles", postcards.size());

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
        {
            for (ServerPlayerEntity player : server.getPlayerList().getPlayers())
            {
                SimpleNetworkHandler.CHANNEL.sendTo(new PostcardReloadMessage(), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    public void getPostcardsFromServer(Map<ResourceLocation, PostcardStyle> postcards)
    {
        this.postcards = postcards;
        Contact.info("Reloaded %d postcard styles from server", postcards.size());
    }

    public Map<ResourceLocation, PostcardStyle> getPostcards()
    {
        return postcards;
    }

    public PostcardStyle getPostcard(ResourceLocation id)
    {
        return postcards.getOrDefault(id, PostcardStyle.DEFAULT);
    }
}
