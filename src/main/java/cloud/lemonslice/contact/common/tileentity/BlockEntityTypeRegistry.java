package cloud.lemonslice.contact.common.tileentity;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.silveroak.common.item.SilveroakRegistry;
import net.minecraft.block.entity.BlockEntityType;

import static cloud.lemonslice.contact.common.block.BlockRegistry.*;

public final class BlockEntityTypeRegistry
{
    public static final BlockEntityType<MailboxBlockEntity> MAILBOX_BLOCK_ENTITY = SilveroakRegistry.registerBlockEntity(Contact.getIdentifier("mailbox"),
            BlockEntityType.Builder.create(MailboxBlockEntity::new,
                    ORANGE_MAILBOX, MAGENTA_MAILBOX, LIGHT_BLUE_MAILBOX, YELLOW_MAILBOX,
                    LIME_MAILBOX, PINK_MAILBOX, GRAY_MAILBOX, LIGHT_GRAY_MAILBOX,
                    CYAN_MAILBOX, PURPLE_MAILBOX, BLUE_MAILBOX, BROWN_MAILBOX,
                    GREEN_MAILBOX, RED_MAILBOX, BLACK_MAILBOX, WHITE_MAILBOX));
}
