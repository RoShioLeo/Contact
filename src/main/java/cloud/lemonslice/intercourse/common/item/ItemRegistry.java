package cloud.lemonslice.intercourse.common.item;

import cloud.lemonslice.intercourse.registry.RegistryModule;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.item.Item;

import static cloud.lemonslice.intercourse.Intercourse.ITEM_GROUP;

public final class ItemRegistry extends RegistryModule
{
    public static final Item MAIL = new NormalItem("mail", ITEM_GROUP);
    public static final Item OPENED_MAIL = new NormalItem("opened_mail", ITEM_GROUP);

    public static final Item PARCEL = new ParcelItem("parcel", false);
    public static final Item ENDER_PARCEL = new ParcelItem("ender_parcel", true);

    public static final Item POSTCARD = new PostcardItem("postcard", false);
    public static final Item ENDER_POSTCARD = new PostcardItem("ender_postcard", true);

    public static final Item WRAPPING_PAPER = new WrappingPaperItem("wrapping_paper");
    public static final Item ENDER_WRAPPING_PAPER = new WrappingPaperItem("ender_wrapping_paper");
}
