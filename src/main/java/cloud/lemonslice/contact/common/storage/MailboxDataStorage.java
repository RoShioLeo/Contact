package cloud.lemonslice.contact.common.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import static cloud.lemonslice.contact.Contact.MODID;

public class MailboxDataStorage extends PersistentState
{
    public final PlayerMailboxData PLAYERS_DATA = new PlayerMailboxData();

    public PlayerMailboxData getData()
    {
        return PLAYERS_DATA;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtCompound compound = new NbtCompound();
        PLAYERS_DATA.writeToNBT(compound);
        return compound;
    }

    public static MailboxDataStorage readFromNbt(NbtCompound tag)
    {
        MailboxDataStorage serverState = new MailboxDataStorage();
        serverState.PLAYERS_DATA.readFromNBT(tag);
        return serverState;
    }

    public static MailboxDataStorage getMailboxData(MinecraftServer server)
    {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        return persistentStateManager.getOrCreate(MailboxDataStorage::readFromNbt, MailboxDataStorage::new, MODID);
    }

}
