package cloud.lemonslice.contact.client.color.item;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;

public class MailboxItemColor implements IItemColor
{

    @Override
    public int getColor(ItemStack itemStack, int tintIndex)
    {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block instanceof MailboxBlock)
        {
            if (tintIndex <= 1)
            {
                return ((MailboxBlock) block).boxColor.getColorValue();
            }
            else if (tintIndex == 2)
            {
                return DyeColor.RED.getColorValue();
            }
        }
        return -1;
    }
}
