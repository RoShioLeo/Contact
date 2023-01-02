package cloud.lemonslice.contact.common.screenhandler;

import cloud.lemonslice.contact.common.item.IMailItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.GREEN_POSTBOX_CONTAINER;
import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.RED_POSTBOX_CONTAINER;

public class PostboxScreenHandler extends ScreenHandler
{
    public final SimpleInventory parcel = new SimpleInventory(1);
    public byte status = 0; // 0 for parcel-waiting, 1 for addressee-waiting, 2 for send-ready, 3 for not-found, 4 for full-mailbox, 5 for successful
    public int time = 0;
    public String playerName = "";

    public PostboxScreenHandler(int id, PlayerInventory inv, boolean isRed)
    {
        super(isRed ? RED_POSTBOX_CONTAINER : GREEN_POSTBOX_CONTAINER, id);
        parcel.addListener(inventory ->
        {
            if (parcel.getStack(0).getItem() instanceof IMailItem)
            {
                status = 1;
            }
            else if (status != 5)
            {
                status = 0;
            }
        });
        addSlot(new Slot(parcel, 0, 16, 33)
        {
            @Override
            public boolean canInsert(ItemStack stack)
            {
                return stack.getItem() instanceof IMailItem;
            }
        });
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
    public ItemStack quickMove(PlayerEntity player, int index)
    {
        Slot slot = this.slots.get(index);

        if (!slot.hasStack())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getStack(), oldStack = newStack.copy();

        boolean isMerged;

        // 0~1: Input; 1~28: Player Backpack; 28~37: Hot Bar.

        if (index == 0)
        {
            isMerged = insertItem(newStack, 28, 37, true)
                    || insertItem(newStack, 1, 28, false);
        }
        else if (index < 28)
        {
            isMerged = insertItem(newStack, 0, 1, false)
                    || insertItem(newStack, 28, 37, true);
        }
        else
        {
            isMerged = insertItem(newStack, 0, 28, false);
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

    @Override
    public void close(PlayerEntity player)
    {
        if (player instanceof ServerPlayerEntity)
        {
            if (!player.isAlive() || ((ServerPlayerEntity) player).isDisconnected())
            {
                player.dropItem(parcel.getStack(0), false);
                ItemStack cursor = this.getCursorStack();
                if (!cursor.isEmpty())
                {
                    player.dropItem(cursor, false);
                }
            }
            else
            {
                player.getInventory().offerOrDrop(parcel.getStack(0));
                ItemStack cursor = this.getCursorStack();
                if (!cursor.isEmpty())
                {
                    player.getInventory().offerOrDrop(cursor);
                }
            }
            parcel.setStack(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    public boolean isEnderMail()
    {
        Item mail = parcel.getStack(0).getItem();
        return mail instanceof IMailItem && ((IMailItem) mail).isEnderType();
    }
}
