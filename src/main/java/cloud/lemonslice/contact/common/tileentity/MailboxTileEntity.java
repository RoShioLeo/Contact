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
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.load(this.getBlockState(), pkt.getTag());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbtTag = this.save(new CompoundNBT());
        return new SUpdateTileEntityPacket(getBlockPos(), 1, nbtTag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag)
    {
        super.load(state, tag);
        isOpened = tag.getBoolean("IsOpened");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag)
    {
        tag.putBoolean("IsOpened", isOpened);
        return super.save(tag);
    }

    public void refreshStatus()
    {
        if (!level.isClientSide)
        {
            level.getServer().getLevel(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
            {
                boolean now = !data.PLAYERS_DATA.isMailboxEmpty(data.PLAYERS_DATA.getMailboxOwner(level.dimension(), worldPosition));
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
        if (!level.isClientSide)
        {
            if (refreshTicks >= 0)
            {
                refreshTicks--;
            }
            if (needRefresh || refreshTicks == 0)
            {
                refreshStatus();
                BlockState down = level.getBlockState(worldPosition.below());
                if (down.getBlock() instanceof MailboxBlock && down.getValue(OPEN) != isOpened)
                {
                    level.setBlockAndUpdate(worldPosition.below(), down.setValue(OPEN, isOpened));
                    needRefresh = false;
                }
                else
                {
                    needRefresh = false;
                    return;
                }

                clearCache();
                if (getBlockState().getBlock() instanceof MailboxBlock)
                {
                    level.setBlockAndUpdate(worldPosition, getBlockState().setValue(OPEN, isOpened));
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
        if (this.hasLevel() && !this.level.isClientSide)
        {
            SUpdateTileEntityPacket packet = this.getUpdatePacket();
            Stream<ServerPlayerEntity> playerEntity = ((ServerWorld) this.level).getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition.getX() >> 4, this.worldPosition.getZ() >> 4), false);
            for (ServerPlayerEntity player : playerEntity.collect(Collectors.toList()))
            {
                player.connection.send(packet);
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
