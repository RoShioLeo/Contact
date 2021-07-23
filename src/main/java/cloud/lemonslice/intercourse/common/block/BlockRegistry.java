package cloud.lemonslice.intercourse.common.block;

import cloud.lemonslice.intercourse.registry.RegistryModule;
import cloud.lemonslice.silveroak.common.item.NormalBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;

import static cloud.lemonslice.intercourse.Intercourse.ITEM_GROUP;

public final class BlockRegistry extends RegistryModule
{
    public static final Block WHITE_MAILBOX = new MailboxBlock(DyeColor.WHITE);
    public static final Block ORANGE_MAILBOX = new MailboxBlock(DyeColor.ORANGE);
    public static final Block MAGENTA_MAILBOX = new MailboxBlock(DyeColor.MAGENTA);
    public static final Block LIGHT_BLUE_MAILBOX = new MailboxBlock(DyeColor.LIGHT_BLUE);
    public static final Block YELLOW_MAILBOX = new MailboxBlock(DyeColor.YELLOW);
    public static final Block LIME_MAILBOX = new MailboxBlock(DyeColor.LIME);
    public static final Block PINK_MAILBOX = new MailboxBlock(DyeColor.PINK);
    public static final Block GRAY_MAILBOX = new MailboxBlock(DyeColor.GRAY);
    public static final Block LIGHT_GRAY_MAILBOX = new MailboxBlock(DyeColor.LIGHT_GRAY);
    public static final Block CYAN_MAILBOX = new MailboxBlock(DyeColor.CYAN);
    public static final Block PURPLE_MAILBOX = new MailboxBlock(DyeColor.PURPLE);
    public static final Block BLUE_MAILBOX = new MailboxBlock(DyeColor.BLUE);
    public static final Block BROWN_MAILBOX = new MailboxBlock(DyeColor.BROWN);
    public static final Block GREEN_MAILBOX = new MailboxBlock(DyeColor.GREEN);
    public static final Block RED_MAILBOX = new MailboxBlock(DyeColor.RED);
    public static final Block BLACK_MAILBOX = new MailboxBlock(DyeColor.BLACK);

    public static final Block CENTER_MAILBOX = new CenterMailboxBlock();
    public static final Block RED_POSTBOX = new PostboxBlock("red_postbox", true);
    public static final Block GREEN_POSTBOX = new PostboxBlock("green_postbox", false);

    public static final BlockItem WHITE_MAILBOX_ITEM = new NormalBlockItem(WHITE_MAILBOX, ITEM_GROUP);
    public static final BlockItem ORANGE_MAILBOX_ITEM = new NormalBlockItem(ORANGE_MAILBOX, ITEM_GROUP);
    public static final BlockItem MAGENTA_MAILBOX_ITEM = new NormalBlockItem(MAGENTA_MAILBOX, ITEM_GROUP);
    public static final BlockItem LIGHT_BLUE_MAILBOX_ITEM = new NormalBlockItem(LIGHT_BLUE_MAILBOX, ITEM_GROUP);
    public static final BlockItem YELLOW_MAILBOX_ITEM = new NormalBlockItem(YELLOW_MAILBOX, ITEM_GROUP);
    public static final BlockItem LIME_MAILBOX_ITEM = new NormalBlockItem(LIME_MAILBOX, ITEM_GROUP);
    public static final BlockItem PINK_MAILBOX_ITEM = new NormalBlockItem(PINK_MAILBOX, ITEM_GROUP);
    public static final BlockItem GRAY_MAILBOX_ITEM = new NormalBlockItem(GRAY_MAILBOX, ITEM_GROUP);
    public static final BlockItem LIGHT_GRAY_MAILBOX_ITEM = new NormalBlockItem(LIGHT_GRAY_MAILBOX, ITEM_GROUP);
    public static final BlockItem CYAN_MAILBOX_ITEM = new NormalBlockItem(CYAN_MAILBOX, ITEM_GROUP);
    public static final BlockItem PURPLE_MAILBOX_ITEM = new NormalBlockItem(PURPLE_MAILBOX, ITEM_GROUP);
    public static final BlockItem BLUE_MAILBOX_ITEM = new NormalBlockItem(BLUE_MAILBOX, ITEM_GROUP);
    public static final BlockItem BROWN_MAILBOX_ITEM = new NormalBlockItem(BROWN_MAILBOX, ITEM_GROUP);
    public static final BlockItem GREEN_MAILBOX_ITEM = new NormalBlockItem(GREEN_MAILBOX, ITEM_GROUP);
    public static final BlockItem RED_MAILBOX_ITEM = new NormalBlockItem(RED_MAILBOX, ITEM_GROUP);
    public static final BlockItem BLACK_MAILBOX_ITEM = new NormalBlockItem(BLACK_MAILBOX, ITEM_GROUP);

    public static final BlockItem CENTER_MAILBOX_ITEM = new NormalBlockItem(CENTER_MAILBOX, ITEM_GROUP);
    public static final BlockItem RED_POSTBOX_ITEM = new NormalBlockItem(RED_POSTBOX, ITEM_GROUP);
    public static final BlockItem GREEN_POSTBOX_ITEM = new NormalBlockItem(GREEN_POSTBOX, ITEM_GROUP);
}
