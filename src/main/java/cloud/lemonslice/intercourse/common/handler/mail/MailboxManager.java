package cloud.lemonslice.intercourse.common.handler.mail;

import cloud.lemonslice.intercourse.common.capability.MailToBeSent;
import cloud.lemonslice.intercourse.common.capability.PlayerMailboxData;
import cloud.lemonslice.intercourse.common.tileentity.MailboxTileEntity;
import cloud.lemonslice.intercourse.network.ActionMessage;
import cloud.lemonslice.intercourse.network.SimpleNetworkHandler;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

import static cloud.lemonslice.intercourse.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;

@Mod.EventBusSubscriber
public final class MailboxManager
{
    private static final List<MailToBeSent> READY_TO_REMOVE = Lists.newArrayList();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            ServerLifecycleHooks.getCurrentServer().getWorld(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
            {
                for (MailToBeSent mail : data.PLAYERS_DATA.mailList)
                {
                    mail.tick();
                    if (mail.isReady())
                    {
                        UUID uuid = mail.getUUID();
                        data.PLAYERS_DATA.addMailboxContents(uuid, mail.getContents());
                        updateState(uuid, data.PLAYERS_DATA);
                        READY_TO_REMOVE.add(mail);
                    }
                }
                data.PLAYERS_DATA.mailList.removeAll(READY_TO_REMOVE);
                READY_TO_REMOVE.clear();
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        event.getPlayer().getEntityWorld().getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
        {
            data.PLAYERS_DATA.nameToUUID.put(event.getPlayer().getName().getString(), event.getPlayer().getUniqueID());
            if (data.PLAYERS_DATA.uuidToContents.get(event.getPlayer().getUniqueID()) == null)
            {
                data.PLAYERS_DATA.resetMailboxContents(event.getPlayer().getUniqueID());
            }
            else
            {
                if (!data.PLAYERS_DATA.isMailboxEmpty(event.getPlayer().getUniqueID()))
                {
                    SimpleNetworkHandler.CHANNEL.sendTo(new ActionMessage(0), ((ServerPlayerEntity) event.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                }
                updateState(event.getPlayer().getUniqueID(), data.PLAYERS_DATA);
            }

        });
    }

    public static void updateState(UUID uuid, PlayerMailboxData data)
    {
        GlobalPos posData = data.getMailboxPos(uuid);
        if (posData != null)
        {
            updateState(ServerLifecycleHooks.getCurrentServer().getWorld(posData.getDimension()), posData.getPos());
        }
    }

    public static void updateState(World world, BlockPos pos)
    {
        if (world != null)
        {
            if (world.isAreaLoaded(pos, 1))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof MailboxTileEntity)
                {
                    ((MailboxTileEntity) te).refreshStatus();
                }
            }
        }
    }

    public static int getDeliveryTicks(RegistryKey<World> fromWorld, BlockPos fromPos, RegistryKey<World> toWorld, BlockPos toPos)
    {
        int time = 0;
        if (fromWorld != toWorld)
        {
            time += 1200;
        }
        time += 4 * (Math.abs(fromPos.getX() - toPos.getX()) + Math.abs(fromPos.getZ() - toPos.getZ()));
        return time;
    }

    public static int getDeliveryTicks(GlobalPos fromPos, GlobalPos toPos)
    {
        return getDeliveryTicks(fromPos.getDimension(), fromPos.getPos(), toPos.getDimension(), toPos.getPos());
    }
}
