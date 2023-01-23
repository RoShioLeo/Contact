package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.client.item.PackageTooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;

public interface IPackageItem
{
    int getCapacity();

    static void openPackage(IPackageItem item, PlayerEntity user, Hand hand)
    {
        SimpleInventory contents = new SimpleInventory(item.getCapacity());
        ItemStack parcel = user.getStackInHand(hand);
        NbtList list = parcel.getOrCreateNbt().getList("parcel", NbtElement.COMPOUND_TYPE);
        contents.readNbtList(list);
        for (int i = 0; i < item.getCapacity(); ++i)
        {
            user.getInventory().offerOrDrop(contents.getStack(i));
        }
    }

    static PackageTooltipData getTooltipData(IPackageItem item, ItemStack stack)
    {
        SimpleInventory contents = new SimpleInventory(item.getCapacity());
        NbtList list = stack.getOrCreateNbt().getList("parcel", NbtElement.COMPOUND_TYPE);
        contents.readNbtList(list);
        return new PackageTooltipData(contents.stacks);
    }
}
