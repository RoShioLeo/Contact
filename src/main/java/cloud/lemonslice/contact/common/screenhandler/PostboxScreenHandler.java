package cloud.lemonslice.contact.common.screenhandler;

import cloud.lemonslice.contact.common.item.IMailItem;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.GREEN_POSTBOX_CONTAINER;
import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.RED_POSTBOX_CONTAINER;

public class PostboxScreenHandler extends ContentScreenHandler
{
    public final SimpleInventory parcel = new SimpleInventory(1);
    // old: 0 for parcel-waiting, 1 for addressee-waiting, 2 for send-ready, 3 for not-found, 4 for full-mailbox, 5 for successful
    // new: 0: waiting mail; 1: writing addressee; 2: successfully sent; 3: cannot send
    public byte status = 0;
    public String playerName = "";
    public List<String> names = Lists.newArrayList();
    public List<Integer> ticks = Lists.newArrayList();

    public PostboxScreenHandler(int id, PlayerInventory inv, boolean isRed)
    {
        super(isRed ? RED_POSTBOX_CONTAINER : GREEN_POSTBOX_CONTAINER, id);
        parcel.addListener(inventory ->
        {
            if (parcel.getStack(0).getItem() instanceof IMailItem)
            {
                if (!parcel.getStack(0).getOrCreateNbt().contains("Sender"))
                {
                    status = 1;
                }
                else
                {
                    status = 3;
                }
            }
            else if (status != 2)
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

    @Override
    public int getContainerCount()
    {
        return 1;
    }
}
