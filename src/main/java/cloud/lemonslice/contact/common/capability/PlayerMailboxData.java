package cloud.lemonslice.contact.common.capability;

import cloud.lemonslice.contact.common.tileentity.MailboxTileEntity;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerMailboxData
{
    public final Map<String, UUID> nameToUUID = Maps.newHashMap();
    public final Map<UUID, ItemStackHandler> uuidToContents = Maps.newHashMap();
    private final Map<UUID, GlobalPos> uuidToLocation = Maps.newHashMap();
    private final Map<GlobalPos, UUID> locationToPlayer = Maps.newHashMap();

    public final List<MailToBeSent> mailList = Lists.newArrayList();

    public ItemStackHandler getMailboxContents(UUID uuid)
    {
        return uuidToContents.getOrDefault(uuid, new ItemStackHandler(24));
    }

    public boolean isMailboxEmpty(UUID uuid)
    {
        ItemStackHandler contents = uuidToContents.get(uuid);
        if (contents == null)
        {
            return true;
        }
        else
        {
            for (int i = 0; i < contents.getSlots(); ++i)
            {
                if (!contents.getStackInSlot(i).isEmpty())
                {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean isMailboxFull(UUID uuid)
    {
        ItemStackHandler contents = uuidToContents.get(uuid);
        if (contents == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < contents.getSlots(); ++i)
            {
                if (contents.getStackInSlot(i).isEmpty())
                {
                    return false;
                }
            }
        }
        return true;
    }

    // Remember to update blockstate
    public void addMailboxContents(UUID uuid, ItemStack parcelIn)
    {
        ItemStackHandler mailbox = getMailboxContents(uuid);
        if (!isMailboxFull(uuid))
        {
            for (int i = 0; i < mailbox.getSlots(); ++i)
            {
                if (mailbox.getStackInSlot(i).isEmpty())
                {
                    mailbox.setStackInSlot(i, parcelIn);
                    setMailboxContents(uuid, mailbox);
                    ServerPlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
                    if (player != null)
                    {
                        SimpleNetworkHandler.CHANNEL.sendTo(new ActionMessage(0), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                    }
                    return;
                }
            }
        }
    }

    // Remember to update blockstate
    public void setMailboxContents(UUID uuid, ItemStackHandler contents)
    {
        uuidToContents.put(uuid, contents);
    }

    public void resetMailboxContents(UUID uuid)
    {
        setMailboxContents(uuid, new ItemStackHandler(24));
    }

    @Nullable
    public UUID getMailboxOwner(RegistryKey<World> world, BlockPos pos)
    {
        return locationToPlayer.get(GlobalPos.of(world, pos));
    }

    @Nullable
    public GlobalPos getMailboxPos(UUID uuid)
    {
        return uuidToLocation.get(uuid);
    }

    public void setMailboxData(UUID uuid, RegistryKey<World> world, BlockPos pos)
    {
        GlobalPos newPos = GlobalPos.of(world, pos);
        GlobalPos oldPos = uuidToLocation.get(uuid);

        if (oldPos != null)
        {
            locationToPlayer.remove(oldPos);
            World oldWorld = ServerLifecycleHooks.getCurrentServer().getLevel(oldPos.dimension());
            if (oldWorld != null && oldWorld.isAreaLoaded(oldPos.pos(), 1))
            {
                TileEntity oldTE = oldWorld.getBlockEntity(oldPos.pos());
                if (oldTE instanceof MailboxTileEntity)
                {
                    ((MailboxTileEntity) oldTE).refreshStatus();
                }
            }
        }
        uuidToLocation.put(uuid, newPos);
        locationToPlayer.put(newPos, uuid);

        World newWorld = ServerLifecycleHooks.getCurrentServer().getLevel(world);
        if (newWorld != null && newWorld.isAreaLoaded(newPos.pos(), 1))
        {
            TileEntity newTE = newWorld.getBlockEntity(newPos.pos());
            if (newTE instanceof MailboxTileEntity)
            {
                ((MailboxTileEntity) newTE).refreshStatus();
            }
        }
    }

    public void removeMailboxData(GlobalPos pos)
    {
        UUID uuid = locationToPlayer.remove(pos);
        if (uuid != null)
        {
            GlobalPos mailboxPos = uuidToLocation.get(uuid);
            if (Objects.equals(mailboxPos, pos))
            {
                uuidToLocation.remove(uuid);
            }
        }
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt)
    {
        int n = uuidToContents.keySet().size();
        nbt.putInt("MapDataSize", n);
        int i = 0;
        for (UUID uuid : uuidToContents.keySet())
        {
            CompoundNBT tag = new CompoundNBT();

            tag.putString("UUID", uuid.toString());
            tag.put("Contents", uuidToContents.getOrDefault(uuid, new ItemStackHandler(24)).serializeNBT());

            GlobalPos globalPos = uuidToLocation.get(uuid);
            if (globalPos != null)
            {
                ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, globalPos.dimension().location()).resultOrPartial(LogManager.getLogger()::error).ifPresent(world -> tag.put("MailboxDimension", world));
                tag.putInt("MailboxX", globalPos.pos().getX());
                tag.putInt("MailboxY", globalPos.pos().getY());
                tag.putInt("MailboxZ", globalPos.pos().getZ());
            }
            nbt.put("MapData" + i, tag);
            i++;
        }

        nbt.putInt("MailListSize", mailList.size());
        for (i = 0; i < mailList.size(); i++)
        {
            nbt.put("MailListData" + i, mailList.get(i).writeToNBT());
        }

        nbt.putInt("NameMapSize", nameToUUID.keySet().size());
        i = 0;
        for (String name : nameToUUID.keySet())
        {
            nbt.putString("NameMap" + i, name);
            nbt.putString("NameMapUUID" + i, nameToUUID.get(name).toString());
            i++;
        }

        return nbt;
    }

    public void readFromNBT(CompoundNBT nbt)
    {
        uuidToContents.clear();
        uuidToLocation.clear();
        locationToPlayer.clear();
        mailList.clear();
        nameToUUID.clear();

        int n = nbt.getInt("MapDataSize");
        for (int i = 0; i < n; i++)
        {
            CompoundNBT tag = nbt.getCompound("MapData" + i);
            UUID uuid = UUID.fromString(tag.getString("UUID"));
            ItemStackHandler contents = new ItemStackHandler(24);
            contents.deserializeNBT(tag.getCompound("Contents"));
            uuidToContents.put(uuid, contents);

            if (tag.contains("MailboxDimension"))
            {
                BlockPos mailboxPos = new BlockPos(tag.getInt("MailboxX"), tag.getInt("MailboxY"), tag.getInt("MailboxZ"));
                RegistryKey<World> mailboxWorld = World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, tag.get("MailboxDimension")).resultOrPartial(LogManager.getLogger()::error).orElse(World.OVERWORLD);
                GlobalPos globalPos = GlobalPos.of(mailboxWorld, mailboxPos);
                uuidToLocation.put(uuid, globalPos);
                locationToPlayer.put(globalPos, uuid);
            }
        }

        n = nbt.getInt("MailListSize");
        for (int i = 0; i < n; i++)
        {
            CompoundNBT tag = nbt.getCompound("MailListData" + i);
            MailToBeSent mail = new MailToBeSent(tag);
            mailList.add(mail);
        }

        n = nbt.getInt("NameMapSize");
        for (int i = 0; i < n; i++)
        {
            String name = nbt.getString("NameMap" + i);
            UUID uuid = UUID.fromString(nbt.getString("NameMapUUID" + i));
            nameToUUID.put(name, uuid);
        }
    }
}
