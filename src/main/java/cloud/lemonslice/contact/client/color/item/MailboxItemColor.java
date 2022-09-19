package cloud.lemonslice.contact.client.color.item;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class MailboxItemColor implements ItemColor
{

    @Override
    public int getColor(ItemStack itemStack, int tintIndex)
    {
        Block block = Block.byItem(itemStack.getItem());
        if (block instanceof MailboxBlock)
        {
            if (tintIndex <= 1)
            {
                return ((MailboxBlock) block).boxColor.getMaterialColor().col;
            }
            else if (tintIndex == 2)
            {
                return DyeColor.RED.getMaterialColor().col;
            }
        }
        return -1;
    }
}
