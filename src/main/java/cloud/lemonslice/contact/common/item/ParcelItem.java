package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ParcelItem extends NormalItem implements IMailItem
{
    private final boolean isEnderType;

    public ParcelItem(boolean isEnderType)
    {
        super(new Item.Properties().tab(Contact.ITEM_GROUP).stacksTo(1));
        this.isEnderType = isEnderType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStackHandler contents = new ItemStackHandler(4);
        ItemStack parcel = playerIn.getItemInHand(handIn);
        contents.deserializeNBT(parcel.getOrCreateTag());
        for (int i = 0; i < 4; ++i)
        {
            playerIn.getInventory().placeItemBackInInventory(contents.getStackInSlot(i));
        }
        return InteractionResultHolder.success(ItemStack.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        this.addSenderInfoTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isEnderType()
    {
        return isEnderType;
    }
}
