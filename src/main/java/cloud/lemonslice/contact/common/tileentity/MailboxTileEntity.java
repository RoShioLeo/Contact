package cloud.lemonslice.contact.common.tileentity;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cloud.lemonslice.contact.common.block.MailboxBlock.OPEN;
import static cloud.lemonslice.contact.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;
import static cloud.lemonslice.contact.common.tileentity.TileEntityTypeRegistry.MAILBOX;

public class MailboxTileEntity extends TileEntity implements ITickableTileEntity
{
    private boolean isOpened = false;
    private boolean needRefresh = false;
    private int refreshTicks = 20;
    private int angel = 0;

    public MailboxTileEntity()
    {
        super(MAILBOX);
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.read(this.getBlockState(), pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbtTag = this.write(new CompoundNBT());
        return new SUpdateTileEntityPacket(getPos(), 1, nbtTag);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag)
    {
        super.read(state, tag);
        isOpened = tag.getBoolean("IsOpened");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        tag.putBoolean("IsOpened", isOpened);
        return super.write(tag);
    }

    public void refreshStatus()
    {
        if (!world.isRemote)
        {
            world.getServer().getWorld(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
            {
                boolean now = !data.PLAYERS_DATA.isMailboxEmpty(data.PLAYERS_DATA.getMailboxOwner(world.getDimensionKey(), pos));
                if (now != isOpened)
                {
                    needRefresh = true;
                    isOpened = now;
                    refresh();
                }
            });
        }
    }

    @Override
    public void tick()
    {
        if (!world.isRemote)
        {
            if (refreshTicks >= 0)
            {
                refreshTicks--;
            }
            if (needRefresh || refreshTicks == 0)
            {
                refreshStatus();
                BlockState down = world.getBlockState(pos.down());
                if (down.getBlock() instanceof MailboxBlock && down.get(OPEN) != isOpened)
                {
                    world.setBlockState(pos.down(), down.with(OPEN, isOpened));
                    needRefresh = false;
                }
                else
                {
                    needRefresh = false;
                    return;
                }

                updateContainingBlockInfo();
                if (getBlockState().getBlock() instanceof MailboxBlock)
                {
                    world.setBlockState(pos, getBlockState().with(OPEN, isOpened));
                }
            }
        }
        else if (isOpened)
        {
            angel++;
            angel %= 40;
        }
    }

    private void refresh()
    {
        if (this.hasWorld() && !this.world.isRemote)
        {
            SUpdateTileEntityPacket packet = this.getUpdatePacket();
            Stream<ServerPlayerEntity> playerEntity = ((ServerWorld) this.world).getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(this.pos.getX() >> 4, this.pos.getZ() >> 4), false);
            for (ServerPlayerEntity player : playerEntity.collect(Collectors.toList()))
            {
                player.connection.sendPacket(packet);
            }
        }
    }

    public boolean isOpened()
    {
        return isOpened;
    }

    public int getAngel()
    {
        return angel;
    }
}
