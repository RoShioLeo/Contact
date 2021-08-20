package cloud.lemonslice.contact.resourse;

import cloud.lemonslice.contact.network.PostcardReloadMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;

@Mod.EventBusSubscriber
public final class PostcardHandler
{
    public static final PostcardManager POSTCARD_MANAGER = new PostcardManager();

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event)
    {
        event.addListener(POSTCARD_MANAGER);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayerEntity && !(event.getPlayer() instanceof FakePlayer))
        {
            SimpleNetworkHandler.CHANNEL.sendTo(new PostcardReloadMessage(), ((ServerPlayerEntity) event.getPlayer()).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static PostcardStyle read(JsonObject json)
    {
        if (!json.has("postcard"))
            throw new JsonSyntaxException("Missing postcard data, expected to find object");

        JsonObject postcardData = JSONUtils.getJsonObject(json, "postcard");
        String cardID = JSONUtils.getString(postcardData, "id");
        int cardWidth = JSONUtils.getInt(postcardData, "width");
        int cardHeight = JSONUtils.getInt(postcardData, "height");

        int textPosX = 10;
        int textPosY = 12;
        int textWidth = 180;
        int textHeight = 108;
        int textColor = 0xff000000;
        if (json.has("text"))
        {
            JsonObject textData = JSONUtils.getJsonObject(json, "text");
            textPosX = JSONUtils.getInt(textData, "x");
            textPosY = JSONUtils.getInt(textData, "y");
            textWidth = JSONUtils.getInt(textData, "width");
            textHeight = JSONUtils.getInt(textData, "height");
            if (textData.has("color"))
            {
                JsonObject color = JSONUtils.getJsonObject(textData, "color");
                int alpha = (JSONUtils.getInt(color, "alpha") & 255) << 24;
                int red = (JSONUtils.getInt(color, "red") & 255) << 16;
                int green = (JSONUtils.getInt(color, "green") & 255) << 8;
                int blue = JSONUtils.getInt(color, "blue") & 255;
                textColor = alpha + red + blue + green;
            }
        }

        String postmarkID = "contact:postmark";
        int postmarkPosX = 142;
        int postmarkPosY = -5;
        int postmarkWidth = 64;
        int postmarkHeight = 52;
        int postmarkColor = textColor;
        if (json.has("postmark"))
        {
            JsonObject postmarkData = JSONUtils.getJsonObject(json, "postmark");
            postmarkPosX = JSONUtils.getInt(postmarkData, "x");
            postmarkPosY = JSONUtils.getInt(postmarkData, "y");
            postmarkWidth = JSONUtils.getInt(postmarkData, "width");
            postmarkHeight = JSONUtils.getInt(postmarkData, "height");
            if (postmarkData.has("color"))
            {
                JsonObject color = JSONUtils.getJsonObject(postmarkData, "color");
                int alpha = (JSONUtils.getInt(color, "alpha") & 255) << 24;
                int red = (JSONUtils.getInt(color, "red") & 255) << 16;
                int green = (JSONUtils.getInt(color, "green") & 255) << 8;
                int blue = JSONUtils.getInt(color, "blue") & 255;
                postmarkColor = alpha + red + blue + green;
            }
        }

        return new PostcardStyle(cardID, cardWidth, cardHeight, textPosX, textPosY, textWidth, textHeight, textColor, postmarkID, postmarkPosX, postmarkPosY, postmarkWidth, postmarkHeight, postmarkColor);
    }

    public static PostcardStyle read(PacketBuffer buffer)
    {
        String cardID = buffer.readString(32767);
        int cardWidth = buffer.readInt();
        int cardHeight = buffer.readInt();

        int textPosX = buffer.readInt();
        int textPosY = buffer.readInt();
        int textWidth = buffer.readInt();
        int textHeight = buffer.readInt();
        int textColor = buffer.readInt();

        String postmarkID = buffer.readString(32767);
        int postmarkPosX = buffer.readInt();
        int postmarkPosY = buffer.readInt();
        int postmarkWidth = buffer.readInt();
        int postmarkHeight = buffer.readInt();
        int postmarkColor = buffer.readInt();

        return new PostcardStyle(cardID, cardWidth, cardHeight, textPosX, textPosY, textWidth, textHeight, textColor, postmarkID, postmarkPosX, postmarkPosY, postmarkWidth, postmarkHeight, postmarkColor);
    }

    public static void write(PacketBuffer buffer, PostcardStyle style)
    {
        buffer.writeString(style.cardID, 32767);
        buffer.writeInt(style.cardWidth);
        buffer.writeInt(style.cardHeight);
        buffer.writeInt(style.textPosX);
        buffer.writeInt(style.textPosY);
        buffer.writeInt(style.textWidth);
        buffer.writeInt(style.textHeight);
        buffer.writeInt(style.textColor);
        buffer.writeString(style.postmarkID, 32767);
        buffer.writeInt(style.postmarkPosX);
        buffer.writeInt(style.postmarkPosY);
        buffer.writeInt(style.postmarkWidth);
        buffer.writeInt(style.postmarkHeight);
        buffer.writeInt(style.postmarkColor);
    }

    public static void serialize(JsonObject json, PostcardStyle style)
    {
        JsonObject postcardData = new JsonObject();
        postcardData.addProperty("id", style.cardID);
        postcardData.addProperty("width", style.cardWidth);
        postcardData.addProperty("height", style.cardHeight);
        json.add("postcard", postcardData);

        JsonObject textData = new JsonObject();
        textData.addProperty("x", style.textPosX);
        textData.addProperty("y", style.textPosY);
        textData.addProperty("width", style.textWidth);
        textData.addProperty("height", style.textHeight);
        JsonObject textColor = new JsonObject();
        textColor.addProperty("alpha", ColorHelper.getAlpha(style.textColor));
        textColor.addProperty("red", ColorHelper.getRed(style.textColor));
        textColor.addProperty("green", ColorHelper.getGreen(style.textColor));
        textColor.addProperty("blue", ColorHelper.getBlue(style.textColor));
        textData.add("color", textColor);
        json.add("text", textData);

        JsonObject postmarkData = new JsonObject();
        postmarkData.addProperty("id", style.postmarkID);
        postmarkData.addProperty("x", style.postmarkPosX);
        postmarkData.addProperty("y", style.postmarkPosY);
        postmarkData.addProperty("width", style.postmarkWidth);
        postmarkData.addProperty("height", style.postmarkHeight);
        JsonObject postmarkColor = new JsonObject();
        postmarkColor.addProperty("alpha", ColorHelper.getAlpha(style.postmarkColor));
        postmarkColor.addProperty("red", ColorHelper.getRed(style.postmarkColor));
        postmarkColor.addProperty("green", ColorHelper.getGreen(style.postmarkColor));
        postmarkColor.addProperty("blue", ColorHelper.getBlue(style.postmarkColor));
        postmarkData.add("color", postmarkColor);
        json.add("postmark", postmarkData);
    }
}
