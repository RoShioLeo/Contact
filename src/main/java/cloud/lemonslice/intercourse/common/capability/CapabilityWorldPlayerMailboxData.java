package cloud.lemonslice.intercourse.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class CapabilityWorldPlayerMailboxData
{
    @CapabilityInject(Data.class)
    public static Capability<Data> WORLD_PLAYERS_DATA;

    public static class Storage implements Capability.IStorage<Data>
    {
        @Override
        public INBT writeNBT(Capability<Data> capability, Data instance, Direction side)
        {
            CompoundNBT compound = new CompoundNBT();
            instance.PLAYERS_DATA.writeToNBT(compound);
            return compound;
        }

        @Override
        public void readNBT(Capability<Data> capability, Data instance, Direction side, INBT nbt)
        {
            instance.PLAYERS_DATA.readFromNBT((CompoundNBT) nbt);
        }
    }

    public static class Data
    {
        public final PlayerMailboxData PLAYERS_DATA = new PlayerMailboxData();
    }

    public static class Provider implements ICapabilitySerializable<INBT>
    {
        private final LazyOptional<Data> data = LazyOptional.of(Data::new);
        private final Capability.IStorage<Data> storage = WORLD_PLAYERS_DATA.getStorage();

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            if (cap.equals(WORLD_PLAYERS_DATA))
                return data.cast();
            else
                return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT()
        {
            return storage.writeNBT(WORLD_PLAYERS_DATA, data.orElse(new Data()), null);
        }

        @Override
        public void deserializeNBT(INBT nbt)
        {
            storage.readNBT(WORLD_PLAYERS_DATA, data.orElse(new Data()), null, nbt);
        }
    }
}
