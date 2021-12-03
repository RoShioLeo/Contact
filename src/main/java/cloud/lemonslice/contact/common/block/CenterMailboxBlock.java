package cloud.lemonslice.contact.common.block;

import cloud.lemonslice.silveroak.common.block.NormalHorizontalBlock;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;

public class CenterMailboxBlock extends NormalHorizontalBlock
{
    public CenterMailboxBlock()
    {
        super(BlockBehaviour.Properties.of(Material.METAL));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClientSide)
        {
            return worldIn.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).map(data ->
            {
                if (data.getData().getMailboxPos(player.getUUID()) == null)
                {
                    ItemStackHandler contents = data.getData().getMailboxContents(player.getUUID());
                    boolean isEmpty = true;
                    for (int i = 0; i < contents.getSlots(); ++i)
                    {
                        if (!contents.getStackInSlot(i).isEmpty())
                        {
                            player.getInventory().placeItemBackInInventory(contents.getStackInSlot(i));
                            isEmpty = false;
                        }
                    }

                    data.getData().resetMailboxContents(player.getUUID());
                    if (!isEmpty)
                    {
                        player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.pick_up"), false);
                    }
                    else
                    {
                        player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.empty"), false);
                    }
                    return InteractionResult.SUCCESS;
                }
                else player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.deny"), false);
                return InteractionResult.FAIL;
            }).orElse(InteractionResult.FAIL);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return Lists.newArrayList(new ItemStack(this));
    }
}
