package cloud.lemonslice.contact.common.tileentity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cloud.lemonslice.contact.Contact.MODID;
import static cloud.lemonslice.contact.common.block.BlockRegistry.*;

public final class BlockEntityTypeRegistry
{

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<MailboxBlockEntity>> MAILBOX_BLOCK_ENTITY = BLOCK_ENTITY_REGISTER.register("mailbox", () -> BlockEntityType.Builder.of(MailboxBlockEntity::new,
            ORANGE_MAILBOX.get(), MAGENTA_MAILBOX.get(), LIGHT_BLUE_MAILBOX.get(), YELLOW_MAILBOX.get(),
            LIME_MAILBOX.get(), PINK_MAILBOX.get(), GRAY_MAILBOX.get(), LIGHT_GRAY_MAILBOX.get(),
            CYAN_MAILBOX.get(), PURPLE_MAILBOX.get(), BLUE_MAILBOX.get(), BROWN_MAILBOX.get(),
            GREEN_MAILBOX.get(), RED_MAILBOX.get(), BLACK_MAILBOX.get(), WHITE_MAILBOX.get()).build(null));
}
