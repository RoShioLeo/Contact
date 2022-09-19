package cloud.lemonslice.contact.client.color.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;

import static cloud.lemonslice.contact.common.item.ItemRegistry.*;

public final class ItemColorsRegistry
{
    public static final ItemColor MAILBOX_COLOR = new MailboxItemColor();

    public static void init()
    {
        Minecraft.getInstance().getItemColors().register(MAILBOX_COLOR,
                ORANGE_MAILBOX_ITEM.get(), MAGENTA_MAILBOX_ITEM.get(), LIGHT_BLUE_MAILBOX_ITEM.get(), YELLOW_MAILBOX_ITEM.get(),
                LIME_MAILBOX_ITEM.get(), PINK_MAILBOX_ITEM.get(), GRAY_MAILBOX_ITEM.get(), LIGHT_GRAY_MAILBOX_ITEM.get(),
                CYAN_MAILBOX_ITEM.get(), PURPLE_MAILBOX_ITEM.get(), BLUE_MAILBOX_ITEM.get(), BROWN_MAILBOX_ITEM.get(),
                GREEN_MAILBOX_ITEM.get(), RED_MAILBOX_ITEM.get(), BLACK_MAILBOX_ITEM.get(), WHITE_MAILBOX_ITEM.get());
    }
}
