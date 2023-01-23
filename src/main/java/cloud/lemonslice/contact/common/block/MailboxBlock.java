package cloud.lemonslice.contact.common.block;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.common.handler.AdvancementManager;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.item.IMailItem;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import cloud.lemonslice.contact.common.tileentity.MailboxBlockEntity;
import cloud.lemonslice.silveroak.SilveroakOutpost;
import cloud.lemonslice.silveroak.common.ISilveroakEntry;
import cloud.lemonslice.silveroak.common.block.DoubleHorizontalBlock;
import cloud.lemonslice.silveroak.helper.VoxelShapeHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

import static cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry.MAILBOX_BLOCK_ENTITY;

public class MailboxBlock extends DoubleHorizontalBlock implements BlockEntityProvider, ISilveroakEntry
{
    public static final BooleanProperty OPEN = Properties.OPEN;
    public final DyeColor boxColor;
    public final DyeColor flagColor;
    public static final VoxelShape LOWER_SHAPE;
    public static final VoxelShape UPPER_SHAPE_NORTH;
    public static final VoxelShape UPPER_SHAPE_EAST;

    public MailboxBlock(DyeColor boxColor, DyeColor flagColor)
    {
        super(Settings.of(Material.STONE, boxColor).nonOpaque().sounds(BlockSoundGroup.STONE).strength(1.5F, 6.0F));
        this.boxColor = boxColor;
        this.flagColor = flagColor;
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.LOWER).with(OPEN, false));
    }

    public MailboxBlock(DyeColor boxColor)
    {
        this(boxColor, DyeColor.RED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        if (state.get(HALF).equals(DoubleBlockHalf.LOWER))
        {
            return getLowerShape();
        }
        else
        {
            return switch (state.get(FACING))
                    {
                        case EAST, WEST -> UPPER_SHAPE_EAST;
                        default -> UPPER_SHAPE_NORTH;
                    };
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(OPEN);
    }

    @Override
    protected VoxelShape getLowerShape()
    {
        return LOWER_SHAPE;
    }

    @Override
    protected VoxelShape getUpperShape()
    {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit)
    {
        if (!world.isClient())
        {
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(world.getServer());
            BlockPos topPos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos : pos.up();
            UUID mailboxOwner = data.getData().getMailboxOwner(world.getRegistryKey(), topPos);
            if (player.isSneaking())
            {
                // 检查邮箱主人，没有的话，录入
                if (mailboxOwner == null)
                {
                    if (data.getData().getMailboxPos(player.getUuid()) == null)
                    {
                        player.sendMessage(Text.translatable("message.contact.mailbox.binding"), false);
                    }
                    else
                    {
                        player.sendMessage(Text.translatable("message.contact.mailbox.switch"), false);
                    }
                    data.getData().setMailboxData(player.getUuid(), world.getRegistryKey(), topPos);
                    MailboxManager.updateState(world, topPos);
                    AdvancementManager.givePlayerAdvancement(world.getServer(), (ServerPlayerEntity) player, new Identifier("contact:root"));
                    return ActionResult.SUCCESS;
                }
            }
            if (Objects.equals(mailboxOwner, player.getUuid()))
            {
                // 获取邮件
                SimpleInventory contents = data.getData().getMailboxContents(mailboxOwner);
                boolean isEmpty = true;
                for (int i = 0; i < contents.size(); ++i)
                {
                    ItemStack parcel = contents.getStack(i);
                    if (!parcel.isEmpty())
                    {
                        if (parcel.getItem() instanceof PostcardItem)
                        {
                            AdvancementManager.givePlayerAdvancement(world.getServer(), (ServerPlayerEntity) player, new Identifier("contact:receive_postcard"));
                        }
                        if (parcel.getOrCreateNbt().contains("AnotherLevel"))
                        {
                            AdvancementManager.givePlayerAdvancement(world.getServer(), (ServerPlayerEntity) player, new Identifier("contact:from_another_world"));
                        }

                        player.getInventory().offerOrDrop(parcel);
                        isEmpty = false;
                    }
                }
                // 腾空邮件列表
                data.getData().resetMailboxContents(mailboxOwner);
                if (!isEmpty)
                {
                    player.sendMessage(Text.translatable("message.contact.mailbox.pick_up"), false);
                }
                else
                {
                    player.sendMessage(Text.translatable("message.contact.mailbox.empty"), false);
                }
                MailboxManager.updateState(world, topPos);
                return ActionResult.SUCCESS;
            }
            else if (player.getStackInHand(handIn).getItem() instanceof IMailItem)
            {
                if (mailboxOwner != null)
                {
                    ItemStack held = player.getStackInHand(handIn).copy();
                    if (!held.getOrCreateNbt().contains("Sender"))
                    {
                        // 不是主人的话，如果有包裹和明信片，那么塞进去
                        if (!data.getData().isMailboxFull(mailboxOwner))
                        {
                            if (world.getBlockEntity(topPos) instanceof MailboxBlockEntity mailbox && mailbox.checkToSend())
                            {
                                held.getOrCreateNbt().putString("Sender", player.getName().getString());
                                data.getData().addMailboxContents(mailboxOwner, held);
                                player.setStackInHand(handIn, ItemStack.EMPTY);
                                player.sendMessage(Text.translatable("message.contact.mailbox.deliver"), false);
                                AdvancementManager.givePlayerAdvancement(player.getServer(), (ServerPlayerEntity) player, new Identifier("contact:send_in_person"));
                                MailboxManager.updateState(world, topPos);
                            }
                            else
                            {
                                player.sendMessage(Text.translatable("message.contact.mailbox.check"), false);
                            }
                        }
                        else
                        {
                            player.sendMessage(Text.translatable("message.contact.mailbox.full"), false);
                        }
                    }
                    else
                    {
                        player.sendMessage(Text.translatable("message.contact.mailbox.used"), false);
                    }
                    return ActionResult.SUCCESS;
                }
                else
                {
                    player.sendMessage(Text.translatable("message.contact.mailbox.no_owner"), false);
                }
                return ActionResult.SUCCESS;
            }
            else if (mailboxOwner != null)
            {
                SilveroakOutpost.getCurrentServer().getUserCache().getByUuid(mailboxOwner).ifPresent(gameProfile ->
                        player.sendMessage(Text.translatable("message.contact.mailbox.others", gameProfile.getName()), false));
                return ActionResult.SUCCESS;
            }
            player.sendMessage(Text.translatable("message.contact.mailbox.no_owner_tips"), false);
            return ActionResult.FAIL;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        super.onBreak(world, pos, state, player);
        if (!world.isClient())
        {
            MailboxDataStorage data = MailboxDataStorage.getMailboxData(world.getServer());
            BlockPos topPos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos : pos.up();
            data.getData().removeMailboxData(GlobalPos.create(world.getRegistryKey(), topPos));
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, MailboxBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> blockEntityType, BlockEntityTicker<? super E> entityTicker)
    {
        return MAILBOX_BLOCK_ENTITY == blockEntityType ? (BlockEntityTicker<A>) entityTicker : null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return MAILBOX_BLOCK_ENTITY.instantiate(pos, state);
    }

    static
    {
        LOWER_SHAPE = VoxelShapeHelper.createVoxelShape(7, 0, 7, 2, 16, 2);
        UPPER_SHAPE_NORTH = VoxelShapeHelper.createVoxelShape(3, 0, 1, 10, 9, 14);
        UPPER_SHAPE_EAST = VoxelShapeHelper.createVoxelShape(1, 0, 3, 14, 9, 10);
    }

    @Override
    public Identifier getRegistryID()
    {
        return Contact.getIdentifier(boxColor.getName() + "_mailbox");
    }
}
