package cloud.lemonslice.contact.common.screenhandler;

import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.item.IPackageItem;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.WRAPPING_PAPER_CONTAINER;

public class WrappingPaperScreenHandler extends PackageScreenHandler
{
    public final static int CONTENT_COUNT = 4;
    public final SimpleInventory inputs = new SimpleInventory(CONTENT_COUNT);
    private final boolean isEnder;

    public WrappingPaperScreenHandler(int id, Inventory inv, boolean isEnder)
    {
        super(WRAPPING_PAPER_CONTAINER, id);
        this.isEnder = isEnder;
        for (int i = 0; i < CONTENT_COUNT; i++)
        {
            addSlot(new Slot(inputs, i, 35 + 20 * i, 32)
            {
                @Override
                public boolean canInsert(ItemStack stack)
                {
                    if (stack.getItem() instanceof IPackageItem)
                        return false;
                    Identifier id = Registries.ITEM.getId(stack.getItem());
                    return !ContactConfig.blacklistID.contains(id.toString()) && !ContactConfig.blacklistID.contains(id.getPath());
                }
            });
        }
        for (int i = 0; i < 3; ++i)
        {

            for (int j = 0; j < 9; ++j)
            {
                addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 67 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            addSlot(new Slot(inv, i, 8 + i * 18, 125));
        }
    }

    @Override
    public SimpleInventory getInventory()
    {
        return inputs;
    }

    @Override
    public ItemStack getUnpackedItem()
    {
        return isEnder ? new ItemStack(ItemRegistry.ENDER_WRAPPING_PAPER) : new ItemStack(ItemRegistry.WRAPPING_PAPER);
    }

    @Override
    public ItemStack getPackedItem()
    {
        return isEnder ? new ItemStack(ItemRegistry.ENDER_PARCEL) : new ItemStack(ItemRegistry.PARCEL);
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public int getContainerCount()
    {
        return CONTENT_COUNT;
    }
}
