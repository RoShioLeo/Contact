package cloud.lemonslice.contact.common.container;

import cloud.lemonslice.contact.common.item.IMailItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

import static cloud.lemonslice.contact.common.container.ContainerTypeRegistry.GREEN_POSTBOX_CONTAINER;
import static cloud.lemonslice.contact.common.container.ContainerTypeRegistry.RED_POSTBOX_CONTAINER;

public class PostboxContainer extends Container
{
    public final ItemStackHandler parcel = new ItemStackHandler()
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            if (parcel.getStackInSlot(slot).getItem() instanceof IMailItem)
            {
                status = 1;
            }
            else if (status != 5)
            {
                status = 0;
            }
        }
    };
    public byte status = 0; // 0 for parcel-waiting, 1 for addressee-waiting, 2 for send-ready, 3 for not-found, 4 for full-mailbox, 5 for successful
    public int time = 0;
    public String playerName = "";

    public PostboxContainer(int id, PlayerInventory inv, boolean isRed)
    {
        super(isRed ? RED_POSTBOX_CONTAINER : GREEN_POSTBOX_CONTAINER, id);
        addSlot(new SlotItemHandler(parcel, 0, 16, 17)
        {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack)
            {
                return stack.getItem() instanceof IMailItem;
            }
        });
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
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getItem(), oldStack = newStack.copy();

        boolean isMerged;

        // 0~1: Input; 1~28: Player Backpack; 28~37: Hot Bar.

        if (index == 0)
        {
            isMerged = moveItemStackTo(newStack, 28, 37, true)
                    || moveItemStackTo(newStack, 1, 28, false);
        }
        else if (index < 28)
        {
            isMerged = moveItemStackTo(newStack, 0, 1, false)
                    || moveItemStackTo(newStack, 28, 37, true);
        }
        else
        {
            isMerged = moveItemStackTo(newStack, 0, 28, false);
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
    public void removed(PlayerEntity playerIn)
    {
        if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected())
        {
            playerIn.drop(parcel.getStackInSlot(0), false);
            parcel.setStackInSlot(0, ItemStack.EMPTY);
        }
        else
        {
            playerIn.inventory.placeItemBackInInventory(playerIn.getCommandSenderWorld(), parcel.getStackInSlot(0));
            parcel.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return true;
    }

    public boolean isEnderMail()
    {
        Item mail = parcel.getStackInSlot(0).getItem();
        return mail instanceof IMailItem && ((IMailItem) mail).isEnderType();
    }
}
