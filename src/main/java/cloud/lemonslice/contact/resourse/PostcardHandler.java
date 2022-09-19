package cloud.lemonslice.contact.resourse;

import cloud.lemonslice.contact.network.PostcardReloadMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

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
        if (event.getEntity() instanceof ServerPlayer && !(event.getEntity() instanceof FakePlayer))
        {
            SimpleNetworkHandler.CHANNEL.sendTo(new PostcardReloadMessage(), ((ServerPlayer) event.getEntity()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static PostcardStyle read(JsonObject json)
    {
        if (!json.has("postcard"))
            throw new JsonSyntaxException("Missing postcard data, expected to find object");

        JsonObject postcardData = GsonHelper.getAsJsonObject(json, "postcard");
        String cardTexture = GsonHelper.getAsString(postcardData, "texture");
        int cardWidth = GsonHelper.getAsInt(postcardData, "width");
        int cardHeight = GsonHelper.getAsInt(postcardData, "height");

        int textPosX = 10;
        int textPosY = 12;
        int textWidth = 180;
        int textHeight = 108;
        int textColor = 0xff000000;
        if (json.has("text"))
        {
            JsonObject textData = GsonHelper.getAsJsonObject(json, "text");
            textPosX = GsonHelper.getAsInt(textData, "x");
            textPosY = GsonHelper.getAsInt(textData, "y");
            textWidth = GsonHelper.getAsInt(textData, "width");
            textHeight = GsonHelper.getAsInt(textData, "height");
            if (textData.has("color"))
            {
                JsonObject color = GsonHelper.getAsJsonObject(textData, "color");
                int alpha = (GsonHelper.getAsInt(color, "alpha") & 255) << 24;
                int red = (GsonHelper.getAsInt(color, "red") & 255) << 16;
                int green = (GsonHelper.getAsInt(color, "green") & 255) << 8;
                int blue = GsonHelper.getAsInt(color, "blue") & 255;
                textColor = alpha + red + blue + green;
            }
        }

        String postmarkTexture = "contact:postmark";
        int postmarkPosX = 142;
        int postmarkPosY = -5;
        int postmarkWidth = 64;
        int postmarkHeight = 52;
        int postmarkColor = textColor;
        if (json.has("postmark"))
        {
            JsonObject postmarkData = GsonHelper.getAsJsonObject(json, "postmark");
            postmarkTexture = GsonHelper.getAsString(postmarkData, "texture");
            postmarkPosX = GsonHelper.getAsInt(postmarkData, "x");
            postmarkPosY = GsonHelper.getAsInt(postmarkData, "y");
            postmarkWidth = GsonHelper.getAsInt(postmarkData, "width");
            postmarkHeight = GsonHelper.getAsInt(postmarkData, "height");
            if (postmarkData.has("color"))
            {
                JsonObject color = GsonHelper.getAsJsonObject(postmarkData, "color");
                int alpha = (GsonHelper.getAsInt(color, "alpha") & 255) << 24;
                int red = (GsonHelper.getAsInt(color, "red") & 255) << 16;
                int green = (GsonHelper.getAsInt(color, "green") & 255) << 8;
                int blue = GsonHelper.getAsInt(color, "blue") & 255;
                postmarkColor = alpha + red + blue + green;
            }
        }

        return new PostcardStyle(cardTexture, cardWidth, cardHeight, textPosX, textPosY, textWidth, textHeight, textColor, postmarkTexture, postmarkPosX, postmarkPosY, postmarkWidth, postmarkHeight, postmarkColor);
    }

    public static PostcardStyle read(FriendlyByteBuf buffer)
    {
        String cardID = buffer.readUtf(32767);
        int cardWidth = buffer.readInt();
        int cardHeight = buffer.readInt();

        int textPosX = buffer.readInt();
        int textPosY = buffer.readInt();
        int textWidth = buffer.readInt();
        int textHeight = buffer.readInt();
        int textColor = buffer.readInt();

        String postmarkID = buffer.readUtf(32767);
        int postmarkPosX = buffer.readInt();
        int postmarkPosY = buffer.readInt();
        int postmarkWidth = buffer.readInt();
        int postmarkHeight = buffer.readInt();
        int postmarkColor = buffer.readInt();

        return new PostcardStyle(cardID, cardWidth, cardHeight, textPosX, textPosY, textWidth, textHeight, textColor, postmarkID, postmarkPosX, postmarkPosY, postmarkWidth, postmarkHeight, postmarkColor);
    }

    public static void write(FriendlyByteBuf buffer, PostcardStyle style)
    {
        buffer.writeUtf(style.cardTexture, 32767);
        buffer.writeInt(style.cardWidth);
        buffer.writeInt(style.cardHeight);
        buffer.writeInt(style.textPosX);
        buffer.writeInt(style.textPosY);
        buffer.writeInt(style.textWidth);
        buffer.writeInt(style.textHeight);
        buffer.writeInt(style.textColor);
        buffer.writeUtf(style.postmarkTexture, 32767);
        buffer.writeInt(style.postmarkPosX);
        buffer.writeInt(style.postmarkPosY);
        buffer.writeInt(style.postmarkWidth);
        buffer.writeInt(style.postmarkHeight);
        buffer.writeInt(style.postmarkColor);
    }

    public static void serialize(JsonObject json, PostcardStyle style)
    {
        JsonObject postcardData = new JsonObject();
        postcardData.addProperty("texture", style.cardTexture);
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
        postmarkData.addProperty("texture", style.postmarkTexture);
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
