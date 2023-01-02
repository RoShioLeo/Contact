package cloud.lemonslice.contact.common.storage;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.UUID;

public class MailToBeSent
{
    private UUID uuid;
    private final SimpleInventory contents;
    private long ticks;

    public MailToBeSent(NbtCompound nbt)
    {
        uuid = UUID.fromString(nbt.getString("MailUUID"));
        ticks = nbt.getInt("MailTicks");
        contents = new SimpleInventory(1);
        contents.readNbtList(nbt.getList("MailContents", NbtElement.COMPOUND_TYPE));
    }

    public MailToBeSent(UUID uuid, ItemStack contents, long ticks)
    {
        this.uuid = uuid;
        this.contents = new SimpleInventory(1);
        this.contents.setStack(0, contents.copy());
        this.ticks = ticks;
    }

    public ItemStack getContents()
    {
        return contents.getStack(0).copy();
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

    public NbtCompound writeToNBT()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("MailUUID", uuid.toString());
        nbt.putLong("MailTicks", ticks);
        nbt.put("MailContents", contents.toNbtList());
        return nbt;
    }
}
