package cloud.lemonslice.contact.resourse;

import cloud.lemonslice.contact.network.PostcardReloadMessage;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.JsonHelper;

public final class PostcardHandler
{
    public static final PostcardManager POSTCARD_MANAGER = new PostcardManager();

    public static void onPlayerLoggedIn(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server)
    {
        ServerPlayNetworking.send(handler.getPlayer(), PostcardReloadMessage.getID(), PostcardReloadMessage.create().toBytes());
    }

    public static PostcardStyle read(JsonObject json)
    {
        if (!json.has("postcard"))
            throw new JsonSyntaxException("Missing postcard data, expected to find object");

        JsonObject postcardData = JsonHelper.getObject(json, "postcard");
        String cardTexture = JsonHelper.getString(postcardData, "texture");
        int cardWidth = JsonHelper.getInt(postcardData, "width");
        int cardHeight = JsonHelper.getInt(postcardData, "height");

        int textPosX = 10;
        int textPosY = 12;
        int textWidth = 180;
        int textHeight = 108;
        int textColor = 0xff000000;
        if (json.has("text"))
        {
            JsonObject textData = JsonHelper.getObject(json, "text");
            textPosX = JsonHelper.getInt(textData, "x");
            textPosY = JsonHelper.getInt(textData, "y");
            textWidth = JsonHelper.getInt(textData, "width");
            textHeight = JsonHelper.getInt(textData, "height");
            if (textData.has("color"))
            {
                JsonObject color = JsonHelper.getObject(textData, "color");
                int alpha = (JsonHelper.getInt(color, "alpha") & 255) << 24;
                int red = (JsonHelper.getInt(color, "red") & 255) << 16;
                int green = (JsonHelper.getInt(color, "green") & 255) << 8;
                int blue = JsonHelper.getInt(color, "blue") & 255;
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
            JsonObject postmarkData = JsonHelper.getObject(json, "postmark");
            postmarkTexture = JsonHelper.getString(postmarkData, "texture");
            postmarkPosX = JsonHelper.getInt(postmarkData, "x");
            postmarkPosY = JsonHelper.getInt(postmarkData, "y");
            postmarkWidth = JsonHelper.getInt(postmarkData, "width");
            postmarkHeight = JsonHelper.getInt(postmarkData, "height");
            if (postmarkData.has("color"))
            {
                JsonObject color = JsonHelper.getObject(postmarkData, "color");
                int alpha = (JsonHelper.getInt(color, "alpha") & 255) << 24;
                int red = (JsonHelper.getInt(color, "red") & 255) << 16;
                int green = (JsonHelper.getInt(color, "green") & 255) << 8;
                int blue = JsonHelper.getInt(color, "blue") & 255;
                postmarkColor = alpha + red + blue + green;
            }
        }

        return new PostcardStyle(cardTexture, cardWidth, cardHeight, textPosX, textPosY, textWidth, textHeight, textColor, postmarkTexture, postmarkPosX, postmarkPosY, postmarkWidth, postmarkHeight, postmarkColor);
    }

    public static PostcardStyle read(PacketByteBuf buffer)
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

    public static void write(PacketByteBuf buffer, PostcardStyle style)
    {
        buffer.writeString(style.cardTexture, 32767);
        buffer.writeInt(style.cardWidth);
        buffer.writeInt(style.cardHeight);
        buffer.writeInt(style.textPosX);
        buffer.writeInt(style.textPosY);
        buffer.writeInt(style.textWidth);
        buffer.writeInt(style.textHeight);
        buffer.writeInt(style.textColor);
        buffer.writeString(style.postmarkTexture, 32767);
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
