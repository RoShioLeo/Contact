package cloud.lemonslice.contact.common.container;

import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.item.ParcelItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

import static cloud.lemonslice.contact.common.container.ContainerTypeRegistry.WRAPPING_PAPER_CONTAINER;

public class WrappingPaperContainer extends AbstractContainerMenu
{
    public final ItemStackHandler inputs = new ItemStackHandler(4);
    private final boolean isEnder;
    public boolean isPacked = false;
    public boolean droppedPaper = false;

    public WrappingPaperContainer(int id, Inventory inv, boolean isEnder)
    {
        super(WRAPPING_PAPER_CONTAINER.get(), id);
        this.isEnder = isEnder;
        for (int i = 0; i < 4; i++)
        {
            addSlot(new SlotItemHandler(inputs, i, 35 + 20 * i, 16)
            {
                @Override
                public boolean mayPlace(@Nonnull ItemStack stack)
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
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getItem(), oldStack = newStack.copy();

        boolean isMerged;

        // 0~4: Input; 4~31: Player Backpack; 31~40: Hot Bar.

        if (index < 4)
        {
            isMerged = moveItemStackTo(newStack, 31, 40, true)
                    || moveItemStackTo(newStack, 4, 31, false);
        }
        else if (index < 31)
        {
            isMerged = moveItemStackTo(newStack, 0, 4, false)
                    || moveItemStackTo(newStack, 31, 40, true);
        }
        else
        {
            isMerged = moveItemStackTo(newStack, 0, 31, false);
        }

        if (!isMerged)
        {
            return ItemStack.EMPTY;
        }

        if (newStack.getCount() == 0)
        {
            slot.set(ItemStack.EMPTY);
        }
        else
        {
            slot.setChanged();
        }

        slot.onTake(playerIn, newStack);

        return oldStack;
    }

    @Override
    public void removed(Player playerIn)
    {
        if (!isPacked)
        {
            if (!playerIn.isAlive() || playerIn instanceof ServerPlayer && ((ServerPlayer) playerIn).hasDisconnected())
            {
                for (int j = 0; j < 4; ++j)
                {
                    playerIn.drop(inputs.getStackInSlot(j), false);
                    inputs.setStackInSlot(j, ItemStack.EMPTY);
                }

                if (!playerIn.getAbilities().instabuild && !droppedPaper)
                {
                    playerIn.drop(isEnder ? new ItemStack(ItemRegistry.ENDER_WRAPPING_PAPER.get()) : new ItemStack(ItemRegistry.WRAPPING_PAPER.get()), false);
                    droppedPaper = true;
                }
            }
            else
            {
                for (int i = 0; i < 4; ++i)
                {
                    playerIn.getInventory().placeItemBackInInventory(inputs.getStackInSlot(i));
                    inputs.setStackInSlot(i, ItemStack.EMPTY);
                }

                if (!playerIn.getAbilities().instabuild && !droppedPaper)
                {
                    playerIn.getInventory().placeItemBackInInventory(isEnder ? new ItemStack(ItemRegistry.ENDER_WRAPPING_PAPER.get()) : new ItemStack(ItemRegistry.WRAPPING_PAPER.get()));
                    droppedPaper = true;
                }
            }
        }
        else
        {
            ItemStack parcel = isEnder ? new ItemStack(ItemRegistry.ENDER_PARCEL.get()) : new ItemStack(ItemRegistry.PARCEL.get());
            parcel.setTag(inputs.serializeNBT());
            if (!playerIn.isAlive() || playerIn instanceof ServerPlayer && ((ServerPlayer) playerIn).hasDisconnected())
            {
                playerIn.drop(parcel, false);
            }
            else
            {
                playerIn.getInventory().placeItemBackInInventory(parcel);
            }
        }
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }
}
