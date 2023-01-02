package cloud.lemonslice.contact.common.handler;

import net.minecraft.advancement.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class AdvancementManager
{
    public static void givePlayerAdvancement(MinecraftServer server, ServerPlayerEntity player, Identifier id)
    {
        Advancement adv = server.getAdvancementLoader().get(id);
        if (adv != null)
        {
            server.getPlayerManager().getAdvancementTracker(player).grantCriterion(adv, "impossible");
        }
    }
}
