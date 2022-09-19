package cloud.lemonslice.contact.client.color.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;

import static cloud.lemonslice.contact.common.block.BlockRegistry.*;

public final class BlockColorsRegistry
{
    public static final BlockColor MAILBOX_COLOR = new MailboxBlockColor();

    public static void init()
    {
        Minecraft.getInstance().getBlockColors().register(MAILBOX_COLOR,
                ORANGE_MAILBOX.get(), MAGENTA_MAILBOX.get(), LIGHT_BLUE_MAILBOX.get(), YELLOW_MAILBOX.get(),
                LIME_MAILBOX.get(), PINK_MAILBOX.get(), GRAY_MAILBOX.get(), LIGHT_GRAY_MAILBOX.get(),
                CYAN_MAILBOX.get(), PURPLE_MAILBOX.get(), BLUE_MAILBOX.get(), BROWN_MAILBOX.get(),
                GREEN_MAILBOX.get(), RED_MAILBOX.get(), BLACK_MAILBOX.get(), WHITE_MAILBOX.get());
    }
}
