package cloud.lemonslice.contact.common;

import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class CommonProxy
{
    public World getClientWorld()
    {
        throw new IllegalStateException("Only run this on the client!");
    }

    public PlayerEntity getClientPlayer()
    {
        throw new IllegalStateException("Only run this on the client!");
    }

    public static void registerCompostable()
    {
//        CHANCES.put(Items.POISONOUS_POTATO, 0.3F);
    }

    public static void registerFireInfo()
    {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
//        fireblock.setFireInfo(BlocksRegistry.WOODEN_FRAME, 5, 20);
    }
}
