package cloud.lemonslice.contact.common.screenhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class ContentScreenHandler extends ScreenHandler
{
    public ContentScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId)
    {
        super(type, syncId);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index)
    {
        Slot slot = this.slots.get(index);

        if (!slot.hasStack())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getStack(), oldStack = newStack.copy();

        boolean isMerged;
        if (index < getContainerCount())
        {
            isMerged = insertItem(newStack, getContainerCount() + 27, getContainerCount() + 36, true)
                    || insertItem(newStack, getContainerCount(), getContainerCount() + 27, false);
        }
        else if (index < getContainerCount() + 27)
        {
            isMerged = insertItem(newStack, 0, getContainerCount(), false)
                    || insertItem(newStack, getContainerCount() + 27, getContainerCount() + 36, true);
        }
        else
        {
            isMerged = insertItem(newStack, 0, getContainerCount() + 27, false);
        }

        if (!isMerged)
        {
            return ItemStack.EMPTY;
        }

        if (newStack.getCount() == 0)
        {
            slot.setStack(ItemStack.EMPTY);
        }
        else
        {
            slot.markDirty();
        }

        slot.onTakeItem(player, newStack);

        return oldStack;
    }

    public abstract int getContainerCount();
}
