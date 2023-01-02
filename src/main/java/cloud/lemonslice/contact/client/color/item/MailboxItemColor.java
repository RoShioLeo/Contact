package cloud.lemonslice.contact.client.color.item;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import net.minecraft.block.Block;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

public class MailboxItemColor implements ItemColorProvider
{
    @Override
    public int getColor(ItemStack itemStack, int tintIndex)
    {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block instanceof MailboxBlock)
        {
            if (tintIndex <= 1)
            {
                return ((MailboxBlock) block).boxColor.getMapColor().color;
            }
            else if (tintIndex == 2)
            {
                return DyeColor.RED.getMapColor().color;
            }
        }
        return -1;
    }
}
