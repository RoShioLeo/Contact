package cloud.lemonslice.contact.common.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cloud.lemonslice.contact.Contact.MODID;

public final class BlockRegistry
{
    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> WHITE_MAILBOX = BLOCK_REGISTER.register(DyeColor.WHITE.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.WHITE));
    public static final RegistryObject<Block> ORANGE_MAILBOX = BLOCK_REGISTER.register(DyeColor.ORANGE.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.ORANGE));
    public static final RegistryObject<Block> MAGENTA_MAILBOX = BLOCK_REGISTER.register(DyeColor.MAGENTA.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.MAGENTA));
    public static final RegistryObject<Block> LIGHT_BLUE_MAILBOX = BLOCK_REGISTER.register(DyeColor.LIGHT_BLUE.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<Block> YELLOW_MAILBOX = BLOCK_REGISTER.register(DyeColor.YELLOW.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.YELLOW));
    public static final RegistryObject<Block> LIME_MAILBOX = BLOCK_REGISTER.register(DyeColor.LIME.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.LIME));
    public static final RegistryObject<Block> PINK_MAILBOX = BLOCK_REGISTER.register(DyeColor.PINK.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.PINK));
    public static final RegistryObject<Block> GRAY_MAILBOX = BLOCK_REGISTER.register(DyeColor.GRAY.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.GRAY));
    public static final RegistryObject<Block> LIGHT_GRAY_MAILBOX = BLOCK_REGISTER.register(DyeColor.LIGHT_GRAY.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<Block> CYAN_MAILBOX = BLOCK_REGISTER.register(DyeColor.CYAN.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.CYAN));
    public static final RegistryObject<Block> PURPLE_MAILBOX = BLOCK_REGISTER.register(DyeColor.PURPLE.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.PURPLE));
    public static final RegistryObject<Block> BLUE_MAILBOX = BLOCK_REGISTER.register(DyeColor.BLUE.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.BLUE));
    public static final RegistryObject<Block> BROWN_MAILBOX = BLOCK_REGISTER.register(DyeColor.BROWN.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.BROWN));
    public static final RegistryObject<Block> GREEN_MAILBOX = BLOCK_REGISTER.register(DyeColor.GREEN.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.GREEN));
    public static final RegistryObject<Block> RED_MAILBOX = BLOCK_REGISTER.register(DyeColor.RED.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.RED));
    public static final RegistryObject<Block> BLACK_MAILBOX = BLOCK_REGISTER.register(DyeColor.BLACK.getSerializedName() + "_mailbox", () -> new MailboxBlock(DyeColor.BLACK));

    public static final RegistryObject<Block> CENTER_MAILBOX = BLOCK_REGISTER.register("center_mailbox", CenterMailboxBlock::new);
    public static final RegistryObject<Block> RED_POSTBOX = BLOCK_REGISTER.register("red_postbox", () -> new PostboxBlock(true));
    public static final RegistryObject<Block> GREEN_POSTBOX = BLOCK_REGISTER.register("green_postbox", () -> new PostboxBlock(false));
}
