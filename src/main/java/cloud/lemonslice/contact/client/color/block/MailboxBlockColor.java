package cloud.lemonslice.contact.client.color.block;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MailboxBlockColor implements BlockColor
{
    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter reader, @Nullable BlockPos pos, int tintIndex)
    {
        Block block = state.getBlock();
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
