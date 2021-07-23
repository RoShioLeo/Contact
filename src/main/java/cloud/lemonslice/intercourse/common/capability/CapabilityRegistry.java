package cloud.lemonslice.intercourse.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cloud.lemonslice.intercourse.Intercourse.MODID;

@Mod.EventBusSubscriber
public final class CapabilityRegistry
{
    public static void init()
    {
        CapabilityManager.INSTANCE.register(CapabilityWorldPlayerMailboxData.Data.class, new CapabilityWorldPlayerMailboxData.Storage(), CapabilityWorldPlayerMailboxData.Data::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
    {

    }

    @SubscribeEvent
    public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<World> event)
    {
        if (event.getObject().getDimensionKey() == World.OVERWORLD)
        {
            event.addCapability(new ResourceLocation(MODID, "players_data"), new CapabilityWorldPlayerMailboxData.Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {

    }
}
