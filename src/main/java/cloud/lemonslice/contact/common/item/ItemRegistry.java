package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cloud.lemonslice.contact.Contact.ITEM_GROUP;
import static cloud.lemonslice.contact.Contact.MODID;
import static cloud.lemonslice.contact.common.block.BlockRegistry.*;

public final class ItemRegistry
{
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> MAIL = ITEM_REGISTER.register("mail", () -> new NormalItem(ITEM_GROUP));
    public static final RegistryObject<Item> OPENED_MAIL = ITEM_REGISTER.register("opened_mail", () -> new NormalItem(ITEM_GROUP));

    public static final RegistryObject<Item> PARCEL = ITEM_REGISTER.register("parcel", () -> new ParcelItem(false));
    public static final RegistryObject<Item> ENDER_PARCEL = ITEM_REGISTER.register("ender_parcel", () -> new ParcelItem(true));

    public static final RegistryObject<Item> POSTCARD = ITEM_REGISTER.register("postcard", () -> new PostcardItem(false));
    public static final RegistryObject<Item> ENDER_POSTCARD = ITEM_REGISTER.register("ender_postcard", () -> new PostcardItem(true));

    public static final RegistryObject<Item> WRAPPING_PAPER = ITEM_REGISTER.register("wrapping_paper", WrappingPaperItem::new);
    public static final RegistryObject<Item> ENDER_WRAPPING_PAPER = ITEM_REGISTER.register("ender_wrapping_paper", WrappingPaperItem::new);

    public static final RegistryObject<BlockItem> WHITE_MAILBOX_ITEM = registerBlockItem(WHITE_MAILBOX);
    public static final RegistryObject<BlockItem> ORANGE_MAILBOX_ITEM = registerBlockItem(ORANGE_MAILBOX);
    public static final RegistryObject<BlockItem> MAGENTA_MAILBOX_ITEM = registerBlockItem(MAGENTA_MAILBOX);
    public static final RegistryObject<BlockItem> LIGHT_BLUE_MAILBOX_ITEM = registerBlockItem(LIGHT_BLUE_MAILBOX);
    public static final RegistryObject<BlockItem> YELLOW_MAILBOX_ITEM = registerBlockItem(YELLOW_MAILBOX);
    public static final RegistryObject<BlockItem> LIME_MAILBOX_ITEM = registerBlockItem(LIME_MAILBOX);
    public static final RegistryObject<BlockItem> PINK_MAILBOX_ITEM = registerBlockItem(PINK_MAILBOX);
    public static final RegistryObject<BlockItem> GRAY_MAILBOX_ITEM = registerBlockItem(GRAY_MAILBOX);
    public static final RegistryObject<BlockItem> LIGHT_GRAY_MAILBOX_ITEM = registerBlockItem(LIGHT_GRAY_MAILBOX);
    public static final RegistryObject<BlockItem> CYAN_MAILBOX_ITEM = registerBlockItem(CYAN_MAILBOX);
    public static final RegistryObject<BlockItem> PURPLE_MAILBOX_ITEM = registerBlockItem(PURPLE_MAILBOX);
    public static final RegistryObject<BlockItem> BLUE_MAILBOX_ITEM = registerBlockItem(BLUE_MAILBOX);
    public static final RegistryObject<BlockItem> BROWN_MAILBOX_ITEM = registerBlockItem(BROWN_MAILBOX);
    public static final RegistryObject<BlockItem> GREEN_MAILBOX_ITEM = registerBlockItem(GREEN_MAILBOX);
    public static final RegistryObject<BlockItem> RED_MAILBOX_ITEM = registerBlockItem(RED_MAILBOX);
    public static final RegistryObject<BlockItem> BLACK_MAILBOX_ITEM = registerBlockItem(BLACK_MAILBOX);

    public static final RegistryObject<BlockItem> CENTER_MAILBOX_ITEM = registerBlockItem(CENTER_MAILBOX);
    public static final RegistryObject<BlockItem> RED_POSTBOX_ITEM = registerBlockItem(RED_POSTBOX);
    public static final RegistryObject<BlockItem> GREEN_POSTBOX_ITEM = registerBlockItem(GREEN_POSTBOX);

    public static RegistryObject<BlockItem> registerBlockItem(RegistryObject<Block> block)
    {
        return ITEM_REGISTER.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(ITEM_GROUP)));
    }
}
