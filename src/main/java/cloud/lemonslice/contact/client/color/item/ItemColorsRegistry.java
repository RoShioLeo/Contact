package cloud.lemonslice.contact.client.color.item;

import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.minecraft.client.color.item.ItemColorProvider;

import static cloud.lemonslice.contact.common.item.ItemRegistry.*;

public final class ItemColorsRegistry
{
    public static final ItemColorProvider MAILBOX_COLOR = new MailboxItemColor();

    public static void init()
    {
        ColorProviderRegistryImpl.ITEM.register(MAILBOX_COLOR,
                ORANGE_MAILBOX_ITEM, MAGENTA_MAILBOX_ITEM, LIGHT_BLUE_MAILBOX_ITEM, YELLOW_MAILBOX_ITEM,
                LIME_MAILBOX_ITEM, PINK_MAILBOX_ITEM, GRAY_MAILBOX_ITEM, LIGHT_GRAY_MAILBOX_ITEM,
                CYAN_MAILBOX_ITEM, PURPLE_MAILBOX_ITEM, BLUE_MAILBOX_ITEM, BROWN_MAILBOX_ITEM,
                GREEN_MAILBOX_ITEM, RED_MAILBOX_ITEM, BLACK_MAILBOX_ITEM, WHITE_MAILBOX_ITEM);
    }
}
