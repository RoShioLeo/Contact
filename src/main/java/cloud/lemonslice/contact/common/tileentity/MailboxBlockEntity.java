package cloud.lemonslice.contact.common.tileentity;

import cloud.lemonslice.contact.common.block.MailboxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static cloud.lemonslice.contact.common.block.MailboxBlock.OPEN;
import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;
import static cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry.MAILBOX_BLOCK_ENTITY;

public class MailboxBlockEntity extends BlockEntity
{
    private boolean isOpened = false;
    private boolean needRefresh = false;
    private int refreshTicks = 20;
    private int angel = 0;

    public MailboxBlockEntity(BlockPos pWorldPosition, BlockState pBlockState)
    {
        super(MAILBOX_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.save(new CompoundTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        this.load(pkt.getTag());
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        isOpened = tag.getBoolean("IsOpened");
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        tag.putBoolean("IsOpened", isOpened);
        return super.save(tag);
    }

    public void refreshStatus()
    {
        if (!level.isClientSide)
        {
            level.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
            {
                boolean now = !data.getData().isMailboxEmpty(data.getData().getMailboxOwner(level.dimension(), worldPosition));
                if (now != isOpened)
                {
                    needRefresh = true;
                    isOpened = now;
                    refresh();
                }
            });
        }
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MailboxBlockEntity pBlockEntity)
    {
        if (!pLevel.isClientSide)
        {
            if (pBlockEntity.refreshTicks >= 0)
            {
                pBlockEntity.refreshTicks--;
            }
            if (pBlockEntity.needRefresh || pBlockEntity.refreshTicks == 0)
            {
                pBlockEntity.refreshStatus();
                BlockState down = pLevel.getBlockState(pPos.below());
                if (down.getBlock() instanceof MailboxBlock && down.getValue(OPEN) != pBlockEntity.isOpened)
                {
                    pLevel.setBlockAndUpdate(pPos.below(), down.setValue(OPEN, pBlockEntity.isOpened));
                    pBlockEntity.needRefresh = false;
                }
                else
                {
                    pBlockEntity.needRefresh = false;
                    return;
                }

                if (pState.getBlock() instanceof MailboxBlock)
                {
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(OPEN, pBlockEntity.isOpened));
                }
            }
        }
        else if (pBlockEntity.isOpened)
        {
            pBlockEntity.angel++;
            pBlockEntity.angel %= 40;
        }
    }

    private void refresh()
    {
        if (this.hasLevel() && !this.level.isClientSide)
        {
            ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.m_195640_(this);
            List<ServerPlayer> players = ((ServerLevel) this.level).getChunkSource().chunkMap.m_183262_(new ChunkPos(this.worldPosition.getX() >> 4, this.worldPosition.getZ() >> 4), false);
            for (ServerPlayer player : players)
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
