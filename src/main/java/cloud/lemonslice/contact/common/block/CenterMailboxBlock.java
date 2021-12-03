package cloud.lemonslice.contact.common.block;

import cloud.lemonslice.silveroak.common.block.NormalHorizontalBlock;
import com.google.common.collect.Lists;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

import static cloud.lemonslice.contact.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;

public class CenterMailboxBlock extends NormalHorizontalBlock
{
    public CenterMailboxBlock()
    {
        super(AbstractBlock.Properties.of(Material.METAL), "center_mailbox");
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isClientSide)
        {
            return worldIn.getServer().getLevel(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).map(data ->
            {
                if (data.PLAYERS_DATA.getMailboxPos(player.getUUID()) == null)
                {
                    ItemStackHandler contents = data.PLAYERS_DATA.getMailboxContents(player.getUUID());
                    boolean isEmpty = true;
                    for (int i = 0; i < contents.getSlots(); ++i)
                    {
                        if (!contents.getStackInSlot(i).isEmpty())
                        {
                            player.inventory.placeItemBackInInventory(worldIn, contents.getStackInSlot(i));
                            isEmpty = false;
                        }
                    }

                    data.PLAYERS_DATA.resetMailboxContents(player.getUUID());
                    if (!isEmpty)
                    {
                        player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.pick_up"), false);
                    }
                    else
                    {
                        player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.empty"), false);
                    }
                    return ActionResultType.SUCCESS;
                }
                else player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.deny"), false);
                return ActionResultType.FAIL;
            }).orElse(ActionResultType.FAIL);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return Lists.newArrayList(new ItemStack(this));
    }
}
