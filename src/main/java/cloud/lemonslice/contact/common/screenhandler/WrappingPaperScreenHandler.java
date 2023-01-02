package cloud.lemonslice.contact.common.screenhandler;

import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.WRAPPING_PAPER_CONTAINER;

public class WrappingPaperScreenHandler extends ScreenHandler
{
    public final SimpleInventory inputs = new SimpleInventory(4);
    public boolean isPacked = false;
    public boolean droppedPaper = false;
    private final boolean isEnder;

    public WrappingPaperScreenHandler(int id, Inventory inv, boolean isEnder)
    {
        super(WRAPPING_PAPER_CONTAINER, id);
        this.isEnder = isEnder;
        for (int i = 0; i < 4; i++)
        {
            addSlot(new Slot(inputs, i, 35 + 20 * i, 32)
            {
                @Override
                public boolean canInsert(ItemStack stack)
                {
                    Identifier id = Registries.ITEM.getId(stack.getItem());
                    return !ContactConfig.blacklistID.contains(id.toString()) || !ContactConfig.blacklistID.contains(id.getPath());
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
    public ItemStack quickMove(PlayerEntity player, int index)
    {
        Slot slot = this.slots.get(index);

        if (!slot.hasStack())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getStack(), oldStack = newStack.copy();

        boolean isMerged;

        // 0~4: Input; 4~31: Player Backpack; 31~40: Hot Bar.

        if (index < 4)
        {
            isMerged = insertItem(newStack, 31, 40, true)
                    || insertItem(newStack, 4, 31, false);
        }
        else if (index < 31)
        {
            isMerged = insertItem(newStack, 0, 4, false)
                    || insertItem(newStack, 31, 40, true);
        }
        else
        {
            isMerged = insertItem(newStack, 0, 31, false);
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
            if (!isPacked)
            {
                if (!player.isAlive() || ((ServerPlayerEntity) player).isDisconnected())
                {
                    for (int j = 0; j < 4; ++j)
                    {
                        player.dropItem(inputs.getStack(j), false);
                        inputs.setStack(j, ItemStack.EMPTY);
                    }

                    ItemStack cursor = this.getCursorStack();
                    if (!cursor.isEmpty())
                    {
                        player.dropItem(cursor, false);
                    }

                    if (!player.getAbilities().creativeMode && !droppedPaper)
                    {
                        player.dropItem(isEnder ? new ItemStack(ItemRegistry.ENDER_WRAPPING_PAPER) : new ItemStack(ItemRegistry.WRAPPING_PAPER), false);
                        droppedPaper = true;
                    }
                }
                else
                {
                    for (int i = 0; i < 4; ++i)
                    {
                        player.getInventory().offerOrDrop(inputs.getStack(i));
                        inputs.setStack(i, ItemStack.EMPTY);
                    }

                    ItemStack cursor = this.getCursorStack();
                    if (!cursor.isEmpty())
                    {
                        player.getInventory().offerOrDrop(cursor);
                    }

                    if (!player.getAbilities().creativeMode && !droppedPaper)
                    {
                        player.getInventory().offerOrDrop(isEnder ? new ItemStack(ItemRegistry.ENDER_WRAPPING_PAPER) : new ItemStack(ItemRegistry.WRAPPING_PAPER));
                        droppedPaper = true;
                    }
                }
            }
            else
            {
                ItemStack parcel = isEnder ? new ItemStack(ItemRegistry.ENDER_PARCEL) : new ItemStack(ItemRegistry.PARCEL);
                parcel.getOrCreateNbt().put("parcel", inputs.toNbtList());
                if (!player.isAlive() || ((ServerPlayerEntity) player).isDisconnected())
                {
                    player.dropItem(parcel, false);
                }
                else
                {
                    player.getInventory().offerOrDrop(parcel);
                }
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }
}
