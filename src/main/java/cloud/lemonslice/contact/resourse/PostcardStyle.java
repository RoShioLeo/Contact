package cloud.lemonslice.contact.resourse;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Objects;

public class PostcardStyle
{
    public static final PostcardStyle DEFAULT = new PostcardStyle("contact:stripes", 200, 133, 10, 12, 180, 108, 0xff77787b, "contact:postmark", 142, -5, 64, 52, 0xcd77787b);
    public final String cardID;
    public final int cardWidth;
    public final int cardHeight;
    public final int textPosX;
    public final int textPosY;
    public final int textWidth;
    public final int textHeight;
    public final int textColor;
    public final String postmarkID;
    public final int postmarkPosX;
    public final int postmarkPosY;
    public final int postmarkWidth;
    public final int postmarkHeight;
    public final int postmarkColor;

    public PostcardStyle(String cardID, int cardWidth, int cardHeight,
                         int textPosX, int textPosY, int textWidth, int textHeight, int textColor,
                         String postmarkID, int postmarkPosX, int postmarkPosY, int postmarkWidth, int postmarkHeight, int postmarkColor)
    {
        this.cardID = cardID;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.textPosX = textPosX;
        this.textPosY = textPosY;
        this.textWidth = textWidth;
        this.textHeight = textHeight;
        this.textColor = textColor;
        this.postmarkID = postmarkID;
        this.postmarkPosX = postmarkPosX;
        this.postmarkPosY = postmarkPosY;
        this.postmarkWidth = postmarkWidth;
        this.postmarkHeight = postmarkHeight;
        this.postmarkColor = postmarkColor;
    }

    public ResourceLocation getCardTexture()
    {
        ResourceLocation origin = new ResourceLocation(cardID);
        return new ResourceLocation(origin.getNamespace(), "textures/postcard/" + origin.getPath() + ".png");
    }

    public ResourceLocation getPostmarkTexture()
    {
        ResourceLocation origin = new ResourceLocation(postmarkID);
        return new ResourceLocation(origin.getNamespace(), "textures/postcard/" + origin.getPath() + ".png");
    }

    public static PostcardStyle fromNBT(CompoundNBT nbt)
    {
        if (nbt.contains("Info"))
        {
            CompoundNBT info = nbt.getCompound("Info");
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
            ResourceLocation cardID = new ResourceLocation(nbt.getString("CardID"));
            return PostcardHandler.POSTCARD_MANAGER.getPostcards().getOrDefault(cardID, DEFAULT);
        }
        else return DEFAULT;
    }

    public void toNBT(CompoundNBT nbt)
    {
        for (Map.Entry<ResourceLocation, PostcardStyle> entry : PostcardHandler.POSTCARD_MANAGER.getPostcards().entrySet())
        {
            if (Objects.equals(entry.getValue(), this))
            {
                nbt.putString("CardID", entry.getKey().toString());
                return;
            }
        }

        CompoundNBT info = new CompoundNBT();
        info.putString("ID", this.cardID);
        info.putInt("PosX", this.textPosX);
        info.putInt("PosY", this.textPosY);
        info.putInt("Width", this.textWidth);
        info.putInt("Height", this.textHeight);
        info.putInt("Color", this.textColor);
        nbt.put("Info", info);
    }
}
