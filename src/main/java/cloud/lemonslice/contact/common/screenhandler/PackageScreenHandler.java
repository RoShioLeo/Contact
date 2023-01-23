package cloud.lemonslice.contact.common.screenhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public abstract class PackageScreenHandler extends ContentScreenHandler
{
    public boolean isPacked = false;
    public boolean droppedPaper = false;

    public PackageScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId)
    {
        super(type, syncId);
    }

    public abstract SimpleInventory getInventory();

    public abstract ItemStack getUnpackedItem();

    public abstract ItemStack getPackedItem();

    @Override
    public void close(PlayerEntity player)
    {
        if (player instanceof ServerPlayerEntity)
        {
            if (!isPacked)
            {
                if (!player.isAlive() || ((ServerPlayerEntity) player).isDisconnected())
                {
                    for (int j = 0; j < getContainerCount(); ++j)
                    {
                        player.dropItem(getInventory().getStack(j), false);
                        getInventory().setStack(j, ItemStack.EMPTY);
                    }

                    ItemStack cursor = this.getCursorStack();
                    if (!cursor.isEmpty())
                    {
                        player.dropItem(cursor, false);
                    }

                    if (!player.getAbilities().creativeMode && !droppedPaper)
                    {
                        player.dropItem(getUnpackedItem(), false);
                        droppedPaper = true;
                    }
                }
                else
                {
                    for (int i = 0; i < getContainerCount(); ++i)
                    {
                        player.getInventory().offerOrDrop(getInventory().getStack(i));
                        getInventory().setStack(i, ItemStack.EMPTY);
                    }

                    ItemStack cursor = this.getCursorStack();
                    if (!cursor.isEmpty())
                    {
                        player.getInventory().offerOrDrop(cursor);
                    }

                    if (!player.getAbilities().creativeMode && !droppedPaper)
                    {
                        player.getInventory().offerOrDrop(getUnpackedItem());
                        droppedPaper = true;
                    }
                }
            }
            else
            {
                ItemStack parcel = getPackedItem();
                parcel.getOrCreateNbt().put("parcel", getInventory().toNbtList());
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
}
