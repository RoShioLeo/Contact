package cloud.lemonslice.contact.common.tileentity;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.List;

import static cloud.lemonslice.contact.common.block.MailboxBlock.OPEN;
import static cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry.MAILBOX_BLOCK_ENTITY;

public class MailboxBlockEntity extends BlockEntity
{
    private boolean isOpened = false;
    private boolean needRefresh = false;
    private int refreshTicks = 20;
    private int angel = 0;

    public MailboxBlockEntity(BlockPos pWorldPosition, BlockState pBlockState)
    {
        super(MAILBOX_BLOCK_ENTITY, pWorldPosition, pBlockState);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return this.createNbtWithIdentifyingData();
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        isOpened = nbt.getBoolean("IsOpened");
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("IsOpened", isOpened);
    }

    public void refreshStatus()
    {
        if (!world.isClient())
        {
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(world.getServer());
            {
                boolean now = !data.getData().isMailboxEmpty(data.getData().getMailboxOwner(world.getRegistryKey(), getPos()));
                if (now != isOpened)
                {
                    needRefresh = true;
                    isOpened = now;
                    refresh();
                }
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, MailboxBlockEntity blockEntity)
    {
        if (!world.isClient())
        {
            if (blockEntity.refreshTicks >= 0)
            {
                blockEntity.refreshTicks--;
            }
            if (blockEntity.needRefresh || blockEntity.refreshTicks == 0)
            {
                blockEntity.refreshStatus();
                BlockState down = world.getBlockState(pos.down());
                if (down.getBlock() instanceof MailboxBlock && down.get(OPEN) != blockEntity.isOpened)
                {
                    world.setBlockState(pos.down(), down.with(OPEN, blockEntity.isOpened));
                    blockEntity.needRefresh = false;
                }
                else
                {
                    blockEntity.needRefresh = false;
                    return;
                }

                if (state.getBlock() instanceof MailboxBlock)
                {
                    world.setBlockState(pos, state.with(OPEN, blockEntity.isOpened));
                }
            }
        }
        else if (blockEntity.isOpened)
        {
            blockEntity.angel++;
            blockEntity.angel %= 40;
        }
    }

    private void refresh()
    {
        if (this.hasWorld() && !this.world.isClient())
        {
            BlockEntityUpdateS2CPacket packet = BlockEntityUpdateS2CPacket.create(this);
            List<ServerPlayerEntity> players = ((ServerWorld) this.world).getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(this.getPos().getX() >> 4, this.getPos().getZ() >> 4), false);
            for (ServerPlayerEntity player : players)
            {
                player.networkHandler.sendPacket(packet);
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
