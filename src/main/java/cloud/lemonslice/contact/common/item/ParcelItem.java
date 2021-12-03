package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ParcelItem extends NormalItem implements IMailItem
{
    private final boolean isEnderType;

    public ParcelItem(String name, boolean isEnderType)
    {
        super(name, NormalItem.getNormalItemProperties(Contact.ITEM_GROUP).stacksTo(1));
        this.isEnderType = isEnderType;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStackHandler contents = new ItemStackHandler(4);
        ItemStack parcel = playerIn.getItemInHand(handIn);
        contents.deserializeNBT(parcel.getOrCreateTag());
        for (int i = 0; i < 4; ++i)
        {
            playerIn.inventory.placeItemBackInInventory(playerIn.getCommandSenderWorld(), contents.getStackInSlot(i));
        }
        return ActionResult.success(ItemStack.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        this.addSenderInfoTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isEnderType()
    {
        return isEnderType;
    }
}
