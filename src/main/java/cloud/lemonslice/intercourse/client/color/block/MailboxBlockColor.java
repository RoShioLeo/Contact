package cloud.lemonslice.intercourse.client.color.block;

import cloud.lemonslice.intercourse.common.block.MailboxBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MailboxBlockColor implements IBlockColor
{
    @Override
    public int getColor(BlockState state, @Nullable IBlockDisplayReader reader, @Nullable BlockPos pos, int tintIndex)
    {
        Block block = state.getBlock();
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
