package cloud.lemonslice.contact.common.block;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import cloud.lemonslice.silveroak.common.block.NormalHorizontalBlock;
import cloud.lemonslice.silveroak.common.inter.ISilveroakEntry;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CenterMailboxBlock extends NormalHorizontalBlock implements ISilveroakEntry
{
    public CenterMailboxBlock()
    {
        super(Settings.create().sounds(BlockSoundGroup.METAL));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient())
        {
            if (ContactConfig.enableCenterMailbox)
            {
                MailboxDataStorage data = MailboxDataStorage.getMailboxData(world.getServer());
                if (data.getData().getMailboxPos(player.getUuid()) == null)
                {
                    SimpleInventory contents = data.getData().getMailboxContents(player.getUuid());
                    boolean isEmpty = true;
                    for (int i = 0; i < contents.size(); ++i)
                    {
                        if (!contents.getStack(i).isEmpty())
                        {
                            player.getInventory().offerOrDrop(contents.getStack(i));
                            isEmpty = false;
                        }
                    }

                    data.getData().resetMailboxContents(player.getUuid());
                    if (!isEmpty)
                    {
                        player.sendMessage(Text.translatable("message.contact.mailbox.pick_up"), true);
                    }
                    else
                    {
                        player.sendMessage(Text.translatable("message.contact.mailbox.empty"), true);
                    }
                    return ActionResult.SUCCESS;
                }
                else
                {
                    player.sendMessage(Text.translatable("message.contact.mailbox.deny"), true);
                }
            }
            else
            {
                player.sendMessage(Text.translatable("message.contact.mailbox.disabled"), true);
            }
            return ActionResult.FAIL;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder)
    {
        return Lists.newArrayList(new ItemStack(this));
    }

    @Override
    public Identifier getRegistryID()
    {
        return Contact.getIdentifier("center_mailbox");
    }
}
