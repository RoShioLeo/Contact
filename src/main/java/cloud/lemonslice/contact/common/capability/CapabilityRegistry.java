package cloud.lemonslice.contact.common.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cloud.lemonslice.contact.Contact.MODID;

@Mod.EventBusSubscriber
public final class CapabilityRegistry
{
    public static final Capability<IMailboxDataStorage> WORLD_MAILBOX_DATA = CapabilityManager.get(new CapabilityToken<>()
    {
    });
    ;

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IMailboxDataStorage.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
    {

    }

    @SubscribeEvent
    public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<Level> event)
    {
        if (event.getObject().dimension() == Level.OVERWORLD)
        {
            event.addCapability(new ResourceLocation(MODID, "players_data"), new MailboxDataStorage.Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {

    }
}
