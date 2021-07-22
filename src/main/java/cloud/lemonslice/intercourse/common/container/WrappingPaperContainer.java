package cloud.lemonslice.intercourse.common.container;

import cloud.lemonslice.intercourse.common.item.ItemsRegistry;
import cloud.lemonslice.intercourse.common.item.ParcelItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

import static cloud.lemonslice.intercourse.common.container.ContainerTypesRegistry.WRAPPING_PAPER_CONTAINER;

public class WrappingPaperContainer extends Container
{
    public final ItemStackHandler inputs = new ItemStackHandler(4);
    public boolean isPacked = false;
    public boolean droppedPaper = false;
    private final boolean isEnder;

    public WrappingPaperContainer(int id, PlayerInventory inv, boolean isEnder)
    {
        super(WRAPPING_PAPER_CONTAINER, id);
        this.isEnder = isEnder;
        for (int i = 0; i < 4; i++)
        {
            addSlot(new SlotItemHandler(inputs, i, 35 + 20 * i, 16)
            {
                @Override
                public boolean isItemValid(@Nonnull ItemStack stack)
                {
                    return !(stack.getItem() instanceof ParcelItem);
                }
            });
        }
        for (int i = 0; i < 3; ++i)
        {

            for (int j = 0; j < 9; ++j)
            {
                addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 51 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            addSlot(new Slot(inv, i, 8 + i * 18, 109));
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        Slot slot = this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getStack(), oldStack = newStack.copy();

        boolean isMerged;

        // 0~4: Input; 4~31: Player Backpack; 31~40: Hot Bar.

        if (index < 4)
        {
            isMerged = mergeItemStack(newStack, 31, 40, true)
                    || mergeItemStack(newStack, 4, 31, false);
        }
        else if (index < 31)
        {
            isMerged = mergeItemStack(newStack, 0, 4, false)
                    || mergeItemStack(newStack, 31, 40, true);
        }
        else
        {
            isMerged = mergeItemStack(newStack, 0, 31, false);
        }

        if (!isMerged)
        {
            return ItemStack.EMPTY;
        }

        if (newStack.getCount() == 0)
        {
            slot.putStack(ItemStack.EMPTY);
        }
        else
        {
            slot.onSlotChanged();
        }

        slot.onTake(playerIn, newStack);

        return oldStack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        if (!isPacked)
        {
            if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected())
            {
                for (int j = 0; j < 4; ++j)
                {
                    playerIn.dropItem(inputs.getStackInSlot(j), false);
                    inputs.setStackInSlot(j, ItemStack.EMPTY);
                }

                if (!playerIn.abilities.isCreativeMode && !droppedPaper)
                {
                    playerIn.dropItem(isEnder ? new ItemStack(ItemsRegistry.ENDER_WRAPPING_PAPER) : new ItemStack(ItemsRegistry.WRAPPING_PAPER), false);
                    droppedPaper = true;
                }
            }
            else
            {
                for (int i = 0; i < 4; ++i)
                {
                    playerIn.inventory.placeItemBackInInventory(playerIn.getEntityWorld(), inputs.getStackInSlot(i));
                    inputs.setStackInSlot(i, ItemStack.EMPTY);
                }

                if (!playerIn.abilities.isCreativeMode && !droppedPaper)
                {
                    playerIn.inventory.placeItemBackInInventory(playerIn.getEntityWorld(), isEnder ? new ItemStack(ItemsRegistry.ENDER_WRAPPING_PAPER) : new ItemStack(ItemsRegistry.WRAPPING_PAPER));
                    droppedPaper = true;
                }
            }
        }
        else
        {
            ItemStack parcel = isEnder ? new ItemStack(ItemsRegistry.ENDER_PARCEL) : new ItemStack(ItemsRegistry.PARCEL);
            parcel.setTag(inputs.serializeNBT());
            if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected())
            {
                playerIn.dropItem(parcel, false);
            }
            else
            {
                playerIn.inventory.placeItemBackInInventory(playerIn.getEntityWorld(), parcel);
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
}
