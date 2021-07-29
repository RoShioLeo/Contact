package cloud.lemonslice.contact.common.handler;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public final class AdvancementManager
{
    public static void givePlayerAdvancement(MinecraftServer server, ServerPlayerEntity player, ResourceLocation id)
    {
        Advancement adv = server.getAdvancementManager().getAdvancement(id);
        if (adv != null)
        {
            server.getPlayerList().getPlayerAdvancements(player).grantCriterion(adv, "impossible");
        }
    }
}
