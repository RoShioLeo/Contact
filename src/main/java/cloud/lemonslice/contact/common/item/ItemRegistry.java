package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.resourse.PostcardHandler;
import cloud.lemonslice.silveroak.common.ISilveroakEntry;
import cloud.lemonslice.silveroak.common.item.NormalBlockItem;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.Contact.ITEM_GROUP;
import static cloud.lemonslice.contact.Contact.MODID;
import static cloud.lemonslice.contact.common.block.BlockRegistry.*;
import static cloud.lemonslice.contact.common.item.PostcardItem.getPostcard;
import static cloud.lemonslice.silveroak.common.item.SilveroakRegistry.registerItem;

public final class ItemRegistry
{
    public static final Item MAIL = new NormalItem(new Identifier(MODID, "mail"), ITEM_GROUP);
    public static final Item OPENED_MAIL = new NormalItem(new Identifier(MODID, "opened_mail"), ITEM_GROUP);

    public static final Item PARCEL = new ParcelItem("parcel", false);
    public static final Item ENDER_PARCEL = new ParcelItem("ender_parcel", true);

    public static final Item POSTCARD = new PostcardItem("postcard", false);
    public static final Item ENDER_POSTCARD = new PostcardItem("ender_postcard", true);

    public static final Item WRAPPING_PAPER = new WrappingPaperItem("wrapping_paper");
    public static final Item ENDER_WRAPPING_PAPER = new WrappingPaperItem("ender_wrapping_paper");

    public static final BlockItem WHITE_MAILBOX_ITEM = createBlockItem(WHITE_MAILBOX);
    public static final BlockItem ORANGE_MAILBOX_ITEM = createBlockItem(ORANGE_MAILBOX);
    public static final BlockItem MAGENTA_MAILBOX_ITEM = createBlockItem(MAGENTA_MAILBOX);
    public static final BlockItem LIGHT_BLUE_MAILBOX_ITEM = createBlockItem(LIGHT_BLUE_MAILBOX);
    public static final BlockItem YELLOW_MAILBOX_ITEM = createBlockItem(YELLOW_MAILBOX);
    public static final BlockItem LIME_MAILBOX_ITEM = createBlockItem(LIME_MAILBOX);
    public static final BlockItem PINK_MAILBOX_ITEM = createBlockItem(PINK_MAILBOX);
    public static final BlockItem GRAY_MAILBOX_ITEM = createBlockItem(GRAY_MAILBOX);
    public static final BlockItem LIGHT_GRAY_MAILBOX_ITEM = createBlockItem(LIGHT_GRAY_MAILBOX);
    public static final BlockItem CYAN_MAILBOX_ITEM = createBlockItem(CYAN_MAILBOX);
    public static final BlockItem PURPLE_MAILBOX_ITEM = createBlockItem(PURPLE_MAILBOX);
    public static final BlockItem BLUE_MAILBOX_ITEM = createBlockItem(BLUE_MAILBOX);
    public static final BlockItem BROWN_MAILBOX_ITEM = createBlockItem(BROWN_MAILBOX);
    public static final BlockItem GREEN_MAILBOX_ITEM = createBlockItem(GREEN_MAILBOX);
    public static final BlockItem RED_MAILBOX_ITEM = createBlockItem(RED_MAILBOX);
    public static final BlockItem BLACK_MAILBOX_ITEM = createBlockItem(BLACK_MAILBOX);

    public static final BlockItem CENTER_MAILBOX_ITEM = createBlockItem(CENTER_MAILBOX);
    public static final BlockItem RED_POSTBOX_ITEM = createBlockItem(RED_POSTBOX);
    public static final BlockItem GREEN_POSTBOX_ITEM = createBlockItem(GREEN_POSTBOX);

    public static BlockItem createBlockItem(Block block)
    {
        if (block instanceof ISilveroakEntry b)
        {
            return new NormalBlockItem(block, b.getRegistryID(), ITEM_GROUP);
        }
        else return new BlockItem(block, new FabricItemSettings());
    }

    public static void initItems()
    {
        registerItem(MAIL);
        registerItem(OPENED_MAIL);
        registerItem(PARCEL);
        registerItem(ENDER_PARCEL);
        registerItem(WRAPPING_PAPER);
        registerItem(ENDER_WRAPPING_PAPER);
        registerItem(POSTCARD);
        registerItem(ENDER_POSTCARD);

        registerItem(WHITE_MAILBOX_ITEM);
        registerItem(ORANGE_MAILBOX_ITEM);
        registerItem(MAGENTA_MAILBOX_ITEM);
        registerItem(LIGHT_BLUE_MAILBOX_ITEM);
        registerItem(YELLOW_MAILBOX_ITEM);
        registerItem(LIME_MAILBOX_ITEM);
        registerItem(PINK_MAILBOX_ITEM);
        registerItem(GRAY_MAILBOX_ITEM);
        registerItem(LIGHT_GRAY_MAILBOX_ITEM);
        registerItem(CYAN_MAILBOX_ITEM);
        registerItem(PURPLE_MAILBOX_ITEM);
        registerItem(BLUE_MAILBOX_ITEM);
        registerItem(BROWN_MAILBOX_ITEM);
        registerItem(GREEN_MAILBOX_ITEM);
        registerItem(RED_MAILBOX_ITEM);
        registerItem(BLACK_MAILBOX_ITEM);

        registerItem(CENTER_MAILBOX_ITEM);
        registerItem(RED_POSTBOX_ITEM);
        registerItem(GREEN_POSTBOX_ITEM);
    }

    public static void initPostcardStyles(ItemGroup.Entries contents)
    {
        for (Identifier id : PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet())
        {
            contents.add(getPostcard(id, false));
        }
        for (Identifier id : PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet())
        {
            contents.add(getPostcard(id, true));
        }
    }
}
