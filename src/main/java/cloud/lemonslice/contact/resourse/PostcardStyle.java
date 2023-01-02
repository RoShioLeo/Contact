package cloud.lemonslice.contact.resourse;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class PostcardStyle
{
    public static final PostcardStyle DEFAULT = new PostcardStyle("contact:stripes", 200, 133, 10, 12, 180, 108, 0xff77787b, "contact:postmark", 142, -5, 64, 52, 0xcd77787b);
    public final String cardTexture;
    public final int cardWidth;
    public final int cardHeight;
    public final int textPosX;
    public final int textPosY;
    public final int textWidth;
    public final int textHeight;
    public final int textColor;
    public final String postmarkTexture;
    public final int postmarkPosX;
    public final int postmarkPosY;
    public final int postmarkWidth;
    public final int postmarkHeight;
    public final int postmarkColor;

    public PostcardStyle(String cardTexture, int cardWidth, int cardHeight,
                         int textPosX, int textPosY, int textWidth, int textHeight, int textColor,
                         String postmarkTexture, int postmarkPosX, int postmarkPosY, int postmarkWidth, int postmarkHeight, int postmarkColor)
    {
        this.cardTexture = cardTexture;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.textPosX = textPosX;
        this.textPosY = textPosY;
        this.textWidth = textWidth;
        this.textHeight = textHeight;
        this.textColor = textColor;
        this.postmarkTexture = postmarkTexture;
        this.postmarkPosX = postmarkPosX;
        this.postmarkPosY = postmarkPosY;
        this.postmarkWidth = postmarkWidth;
        this.postmarkHeight = postmarkHeight;
        this.postmarkColor = postmarkColor;
    }

    public Identifier getCardTexture()
    {
        Identifier origin = new Identifier(cardTexture);
        return new Identifier(origin.getNamespace(), "textures/postcard/" + origin.getPath() + ".png");
    }

    public Identifier getPostmarkTexture()
    {
        Identifier origin = new Identifier(postmarkTexture);
        return new Identifier(origin.getNamespace(), "textures/postcard/" + origin.getPath() + ".png");
    }

    public static PostcardStyle fromNBT(NbtCompound nbt)
    {
        if (nbt.contains("Info"))
        {
            NbtCompound info = nbt.getCompound("Info");
            String id = "contact:" + info.getString("ID");
            int posX = info.getInt("PosX");
            int posY = info.getInt("PosY");
            int textWidth = info.getInt("Width");
            int textHeight = info.getInt("Height");
            int color = info.getInt("Color");
            return new PostcardStyle(id, 200, 133, posX, posY, textWidth, textHeight, color, "contact:postmark", 142, -5, 64, 52, color & 0xcdffffff);
        }
        else if (nbt.contains("CardID"))
        {
            Identifier cardID = new Identifier(nbt.getString("CardID"));
            return PostcardHandler.POSTCARD_MANAGER.getPostcards().getOrDefault(cardID, DEFAULT);
        }
        else return DEFAULT;
    }
}
