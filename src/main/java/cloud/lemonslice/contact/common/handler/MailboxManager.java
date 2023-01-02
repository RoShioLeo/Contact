package cloud.lemonslice.contact.common.handler;

import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.storage.MailToBeSent;
import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import cloud.lemonslice.contact.common.storage.PlayerMailboxData;
import cloud.lemonslice.contact.common.tileentity.MailboxBlockEntity;
import cloud.lemonslice.silveroak.SilveroakOutpost;
import com.google.common.collect.Lists;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public final class MailboxManager
{
    private static final List<MailToBeSent> READY_TO_REMOVE = Lists.newArrayList();
    private static int updateTick = 0;

    public static void onServerTick(MinecraftServer server)
    {
        MailboxDataStorage data = MailboxDataStorage.getMailboxData(server);
        updateTick = ++updateTick % 20;
        if (updateTick == 0)
        {
            for (MailToBeSent mail : data.getData().mailList)
            {
                mail.tick(20);
                if (mail.isReady())
                {
                    UUID uuid = mail.getUUID();
                    if (data.getData().addMailboxContents(uuid, mail.getContents()))
                    {
                        PlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                        if (player != null)
                        {
                            player.sendMessage(Text.translatable("message.contact.mailbox.new_mail"), false);
                        }
                        updateState(uuid, data.getData());
                        READY_TO_REMOVE.add(mail);
                    }
                }
            }
            data.getData().mailList.removeAll(READY_TO_REMOVE);
            READY_TO_REMOVE.clear();
        }
    }

    public static void updateState(UUID uuid, PlayerMailboxData data)
    {
        GlobalPos posData = data.getMailboxPos(uuid);
        if (posData != null)
        {
            updateState(SilveroakOutpost.getCurrentServer().getWorld(posData.getDimension()), posData.getPos());
        }
    }

    @SuppressWarnings("deprecation")
    public static void updateState(World world, BlockPos pos)
    {
        if (world != null)
        {
            if (world.isChunkLoaded(pos))
            {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof MailboxBlockEntity)
                {
                    ((MailboxBlockEntity) te).refreshStatus();
                }
            }
        }
    }

    public static int getDeliveryTicks(RegistryKey<World> fromWorld, BlockPos fromPos, RegistryKey<World> toWorld, BlockPos toPos)
    {
        int time = 0;
        if (fromWorld != toWorld)
        {
            time += ContactConfig.ticksToAnotherWorld;
        }
        int distance = Math.abs(fromPos.getX() - toPos.getX()) + Math.abs(fromPos.getZ() - toPos.getZ());
        if (distance > 9000) distance = 9000;
        time += ContactConfig.postalSpeed * distance;
        return time;
    }

    public static int getDeliveryTicks(GlobalPos fromPos, GlobalPos toPos)
    {
        return getDeliveryTicks(fromPos.getDimension(), fromPos.getPos(), toPos.getDimension(), toPos.getPos());
    }
}
