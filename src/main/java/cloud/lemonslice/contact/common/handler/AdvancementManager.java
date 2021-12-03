package cloud.lemonslice.contact.common.handler;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class AdvancementManager
{
    public static void givePlayerAdvancement(MinecraftServer server, ServerPlayer player, ResourceLocation id)
    {
        Advancement adv = server.getAdvancements().getAdvancement(id);
        if (adv != null)
        {
            server.getPlayerList().getPlayerAdvancements(player).award(adv, "impossible");
        }
    }
}
