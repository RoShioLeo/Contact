package cloud.lemonslice.contact.common.block;

import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

import static cloud.lemonslice.contact.client.ClientProxy.registerCutoutRenderLayer;
import static cloud.lemonslice.silveroak.common.item.SilveroakRegistry.registerBlock;

public final class BlockRegistry
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
    public static final Block RED_POSTBOX = new PostboxBlock(true);
    public static final Block GREEN_POSTBOX = new PostboxBlock(false);

    public static void initBlocks()
    {
        registerBlock(WHITE_MAILBOX);
        registerBlock(ORANGE_MAILBOX);
        registerBlock(MAGENTA_MAILBOX);
        registerBlock(LIGHT_BLUE_MAILBOX);
        registerBlock(YELLOW_MAILBOX);
        registerBlock(LIME_MAILBOX);
        registerBlock(PINK_MAILBOX);
        registerBlock(GRAY_MAILBOX);
        registerBlock(LIGHT_GRAY_MAILBOX);
        registerBlock(CYAN_MAILBOX);
        registerBlock(PURPLE_MAILBOX);
        registerBlock(BLUE_MAILBOX);
        registerBlock(BROWN_MAILBOX);
        registerBlock(GREEN_MAILBOX);
        registerBlock(RED_MAILBOX);
        registerBlock(BLACK_MAILBOX);
        registerBlock(CENTER_MAILBOX);
        registerBlock(RED_POSTBOX);
        registerBlock(GREEN_POSTBOX);
    }

    public static void registerRenderLayer()
    {
        registerCutoutRenderLayer(WHITE_MAILBOX);
        registerCutoutRenderLayer(ORANGE_MAILBOX);
        registerCutoutRenderLayer(MAGENTA_MAILBOX);
        registerCutoutRenderLayer(LIGHT_BLUE_MAILBOX);
        registerCutoutRenderLayer(YELLOW_MAILBOX);
        registerCutoutRenderLayer(LIME_MAILBOX);
        registerCutoutRenderLayer(PINK_MAILBOX);
        registerCutoutRenderLayer(GRAY_MAILBOX);
        registerCutoutRenderLayer(LIGHT_GRAY_MAILBOX);
        registerCutoutRenderLayer(CYAN_MAILBOX);
        registerCutoutRenderLayer(PURPLE_MAILBOX);
        registerCutoutRenderLayer(BLUE_MAILBOX);
        registerCutoutRenderLayer(BROWN_MAILBOX);
        registerCutoutRenderLayer(GREEN_MAILBOX);
        registerCutoutRenderLayer(RED_MAILBOX);
        registerCutoutRenderLayer(BLACK_MAILBOX);
    }
}
