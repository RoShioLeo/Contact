package cloud.lemonslice.contact.client.color.block;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;


public class MailboxBlockColor implements BlockColorProvider
{
    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex)
    {
        Block block = state.getBlock();
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
