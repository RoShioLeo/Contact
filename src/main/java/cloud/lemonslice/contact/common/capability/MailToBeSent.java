package cloud.lemonslice.contact.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.UUID;

public class MailToBeSent
{
    private UUID uuid;
    private final ItemStackHandler contents;
    private long ticks;

    public MailToBeSent(CompoundTag nbt)
    {
        uuid = UUID.fromString(nbt.getString("MailUUID"));
        ticks = nbt.getInt("MailTicks");
        contents = new ItemStackHandler();
        contents.deserializeNBT(nbt.getCompound("MailContents"));
    }

    public MailToBeSent(UUID uuid, ItemStack contents, long ticks)
    {
        this.uuid = uuid;
        this.contents = new ItemStackHandler();
        this.contents.setStackInSlot(0, contents.copy());
        this.ticks = ticks;
    }

    public ItemStack getContents()
    {
        return contents.getStackInSlot(0).copy();
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public boolean isReady()
    {
        return ticks <= 0;
    }

    public void tick(int tick)
    {
        if (ticks > 0) ticks -= tick;
    }

    public CompoundTag writeToNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("MailUUID", uuid.toString());
        nbt.putLong("MailTicks", ticks);
        nbt.put("MailContents", contents.serializeNBT());
        return nbt;
    }

    public void readFromNBT(CompoundTag nbt)
    {
        uuid = UUID.fromString(nbt.getString("MailUUID"));
        ticks = nbt.getLong("MailTicks");
        contents.deserializeNBT(nbt.getCompound("MailContents"));
    }
}
