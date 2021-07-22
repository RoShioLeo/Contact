package cloud.lemonslice.intercourse.common.tileentity;

import cloud.lemonslice.intercourse.registry.RegistryModule;
import net.minecraft.tileentity.TileEntityType;

import static cloud.lemonslice.intercourse.common.block.BlocksRegistry.*;

public final class TileEntityTypesRegistry extends RegistryModule
{
    public static final TileEntityType<MailboxTileEntity> MAILBOX =
            (TileEntityType<MailboxTileEntity>) TileEntityType.Builder.create(MailboxTileEntity::new,
                    ORANGE_MAILBOX, MAGENTA_MAILBOX, LIGHT_BLUE_MAILBOX, YELLOW_MAILBOX,
                    LIME_MAILBOX, PINK_MAILBOX, GRAY_MAILBOX, LIGHT_GRAY_MAILBOX,
                    CYAN_MAILBOX, PURPLE_MAILBOX, BLUE_MAILBOX, BROWN_MAILBOX,
                    GREEN_MAILBOX, RED_MAILBOX, BLACK_MAILBOX, WHITE_MAILBOX)
                    .build(null).setRegistryName("mailbox");
}
