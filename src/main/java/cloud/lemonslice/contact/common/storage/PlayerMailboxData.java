package cloud.lemonslice.contact.common.storage;

import cloud.lemonslice.contact.common.tileentity.MailboxBlockEntity;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.silveroak.SilveroakOutpost;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerMailboxData
{
    public final Map<String, UUID> nameToUUID = Maps.newTreeMap();
    public final Map<UUID, SimpleInventory> uuidToContents = Maps.newHashMap();
    private final Map<UUID, GlobalPos> uuidToLocation = Maps.newHashMap();
    private final Map<GlobalPos, UUID> locationToPlayer = Maps.newHashMap();

    public final List<MailToBeSent> mailList = Lists.newArrayList();

    public SimpleInventory getMailboxContents(UUID uuid)
    {
        return uuidToContents.getOrDefault(uuid, new SimpleInventory(24));
    }

    public boolean isMailboxEmpty(UUID uuid)
    {
        SimpleInventory contents = uuidToContents.get(uuid);
        if (contents == null)
        {
            return true;
        }
        else
        {
            for (int i = 0; i < contents.size(); ++i)
            {
                if (!contents.getStack(i).isEmpty())
                {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean isMailboxFull(UUID uuid)
    {
        SimpleInventory contents = uuidToContents.get(uuid);
        if (contents == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < contents.size(); ++i)
            {
                if (contents.getStack(i).isEmpty())
                {
                    return false;
                }
            }
        }
        return true;
    }

    // Remember to update blockstate
    public boolean addMailboxContents(UUID uuid, ItemStack parcelIn)
    {
        SimpleInventory mailbox = getMailboxContents(uuid);
        if (!isMailboxFull(uuid))
        {
            for (int i = 0; i < mailbox.size(); ++i)
            {
                if (mailbox.getStack(i).isEmpty())
                {
                    mailbox.setStack(i, parcelIn);
                    setMailboxContents(uuid, mailbox);
                    ServerPlayerEntity player = SilveroakOutpost.getCurrentServer().getPlayerManager().getPlayer(uuid);
                    if (player != null)
                    {
                        ServerPlayNetworking.send(player, ActionMessage.getID(), ActionMessage.create(0).toBytes());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Remember to update blockstate
    public void setMailboxContents(UUID uuid, SimpleInventory contents)
    {
        uuidToContents.put(uuid, contents);
    }

    public void resetMailboxContents(UUID uuid)
    {
        setMailboxContents(uuid, new SimpleInventory(24));
    }

    @Nullable
    public UUID getMailboxOwner(RegistryKey<World> world, BlockPos pos)
    {
        return locationToPlayer.get(GlobalPos.create(world, pos));
    }

    @Nullable
    public GlobalPos getMailboxPos(UUID uuid)
    {
        return uuidToLocation.get(uuid);
    }

    @SuppressWarnings("deprecation")
    public void setMailboxData(UUID uuid, RegistryKey<World> world, BlockPos pos)
    {
        GlobalPos newPos = GlobalPos.create(world, pos);
        GlobalPos oldPos = uuidToLocation.get(uuid);

        if (oldPos != null)
        {
            locationToPlayer.remove(oldPos);
            World oldWorld = SilveroakOutpost.getCurrentServer().getWorld(oldPos.getDimension());
            if (oldWorld != null && oldWorld.isChunkLoaded(oldPos.getPos()))
            {
                BlockEntity oldTE = oldWorld.getBlockEntity(oldPos.getPos());
                if (oldTE instanceof MailboxBlockEntity)
                {
                    ((MailboxBlockEntity) oldTE).refreshStatus();
                }
            }
        }
        uuidToLocation.put(uuid, newPos);
        locationToPlayer.put(newPos, uuid);

        World newWorld = SilveroakOutpost.getCurrentServer().getWorld(world);
        if (newWorld != null && newWorld.isChunkLoaded(newPos.getPos()))
        {
            BlockEntity newTE = newWorld.getBlockEntity(newPos.getPos());
            if (newTE instanceof MailboxBlockEntity)
            {
                ((MailboxBlockEntity) newTE).refreshStatus();
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

    public NbtCompound writeToNBT(NbtCompound nbt)
    {
        int n = uuidToContents.keySet().size();
        nbt.putInt("MapDataSize", n);
        int i = 0;
        for (UUID uuid : uuidToContents.keySet())
        {
            NbtCompound tag = new NbtCompound();

            tag.putString("UUID", uuid.toString());
            tag.put("Contents", uuidToContents.getOrDefault(uuid, new SimpleInventory(24)).toNbtList());

            GlobalPos globalPos = uuidToLocation.get(uuid);
            if (globalPos != null)
            {
                Identifier.CODEC.encodeStart(NbtOps.INSTANCE, globalPos.getDimension().getValue()).resultOrPartial(LogManager.getLogger()::error).ifPresent(world -> tag.put("MailboxDimension", world));
                tag.putInt("MailboxX", globalPos.getPos().getX());
                tag.putInt("MailboxY", globalPos.getPos().getY());
                tag.putInt("MailboxZ", globalPos.getPos().getZ());
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

    public void readFromNBT(NbtCompound nbt)
    {
        uuidToContents.clear();
        uuidToLocation.clear();
        locationToPlayer.clear();
        mailList.clear();
        nameToUUID.clear();

        int n = nbt.getInt("MapDataSize");
        for (int i = 0; i < n; i++)
        {
            NbtCompound tag = nbt.getCompound("MapData" + i);
            UUID uuid = UUID.fromString(tag.getString("UUID"));
            SimpleInventory contents = new SimpleInventory(24);
            contents.readNbtList(tag.getList("Contents", NbtElement.COMPOUND_TYPE));
            uuidToContents.put(uuid, contents);

            if (tag.contains("MailboxDimension"))
            {
                BlockPos mailboxPos = new BlockPos(tag.getInt("MailboxX"), tag.getInt("MailboxY"), tag.getInt("MailboxZ"));
                RegistryKey<World> mailboxWorld = World.CODEC.parse(NbtOps.INSTANCE, tag.get("MailboxDimension")).resultOrPartial(LogManager.getLogger()::error).orElse(World.OVERWORLD);
                GlobalPos globalPos = GlobalPos.create(mailboxWorld, mailboxPos);
                uuidToLocation.put(uuid, globalPos);
                locationToPlayer.put(globalPos, uuid);
            }
        }

        n = nbt.getInt("MailListSize");
        for (int i = 0; i < n; i++)
        {
            NbtCompound tag = nbt.getCompound("MailListData" + i);
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
