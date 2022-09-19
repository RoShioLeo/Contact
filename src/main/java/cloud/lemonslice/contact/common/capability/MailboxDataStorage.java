package cloud.lemonslice.contact.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;

public class MailboxDataStorage implements IMailboxDataStorage, INBTSerializable<Tag>
{
    public final PlayerMailboxData PLAYERS_DATA = new PlayerMailboxData();

    @Override
    public PlayerMailboxData getData()
    {
        return PLAYERS_DATA;
    }

    @Override
    public Tag serializeNBT()
    {
        CompoundTag compound = new CompoundTag();
        PLAYERS_DATA.writeToNBT(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(Tag nbt)
    {
        PLAYERS_DATA.readFromNBT((CompoundTag) nbt);
    }

    public static class Provider implements ICapabilitySerializable<Tag>
    {
        private final LazyOptional<MailboxDataStorage> data = LazyOptional.of(MailboxDataStorage::new);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            if (Objects.equals(cap, WORLD_MAILBOX_DATA))
                return data.cast();
            else
                return LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT()
        {
            return data.orElseGet(MailboxDataStorage::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt)
        {
            data.orElseGet(MailboxDataStorage::new).deserializeNBT(nbt);
        }
    }
}
