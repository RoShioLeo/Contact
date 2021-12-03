package cloud.lemonslice.contact.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CommonProxy
{
    public Level getClientWorld()
    {
        throw new IllegalStateException("Only run this on the client!");
    }

    public Player getClientPlayer()
    {
        throw new IllegalStateException("Only run this on the client!");
    }
}
