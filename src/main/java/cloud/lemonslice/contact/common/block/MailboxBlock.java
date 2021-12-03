package cloud.lemonslice.contact.common.block;

import cloud.lemonslice.contact.common.handler.AdvancementManager;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.item.IMailItem;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.common.tileentity.MailboxBlockEntity;
import cloud.lemonslice.silveroak.common.block.NormalHorizontalBlock;
import cloud.lemonslice.silveroak.helper.VoxelShapeHelper;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;
import static cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry.MAILBOX_BLOCK_ENTITY;

public class MailboxBlock extends NormalHorizontalBlock implements EntityBlock
{
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final VoxelShape LOWER_SHAPE;
    public static final VoxelShape UPPER_SHAPE_NORTH;
    public static final VoxelShape UPPER_SHAPE_EAST;

    static
    {
        LOWER_SHAPE = VoxelShapeHelper.createVoxelShape(7, 0, 7, 2, 16, 2);
        UPPER_SHAPE_NORTH = VoxelShapeHelper.createVoxelShape(3, 0, 1, 10, 9, 14);
        UPPER_SHAPE_EAST = VoxelShapeHelper.createVoxelShape(1, 0, 3, 14, 9, 10);
    }

    public final DyeColor boxColor;
    public final DyeColor flagColor;

    public MailboxBlock(DyeColor boxColor, DyeColor flagColor)
    {
        super(BlockBehaviour.Properties.of(Material.STONE, boxColor).noOcclusion().sound(SoundType.STONE).strength(1.5F, 6.0F));
        this.boxColor = boxColor;
        this.flagColor = flagColor;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
    }

    public MailboxBlock(DyeColor boxColor)
    {
        this(boxColor, DyeColor.RED);
    }

    protected static void removeBottomHalf(Level world, BlockPos pos, BlockState state, Player player)
    {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER)
        {
            BlockPos blockpos = pos.below();
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER)
            {
                world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> blockEntityType, BlockEntityTicker<? super E> entityTicker) {
        return MAILBOX_BLOCK_ENTITY.get() == blockEntityType ? (BlockEntityTicker<A>)entityTicker : null;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(HALF).equals(DoubleBlockHalf.LOWER))
        {
            return LOWER_SHAPE;
        }
        else
        {
            switch (state.getValue(FACING))
            {
                case EAST:
                case WEST:
                    return UPPER_SHAPE_EAST;
                default:
                    return UPPER_SHAPE_NORTH;
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, OPEN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP))
        {
            return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        }
        else
        {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClientSide)
        {
            return worldIn.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).map(data ->
            {
                BlockPos topPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
                UUID mailboxOwner = data.getData().getMailboxOwner(worldIn.dimension(), topPos);
                if (player.isShiftKeyDown())
                {
                    // 检查邮箱主人，没有的话，录入
                    if (mailboxOwner == null)
                    {
                        if (data.getData().getMailboxPos(player.getUUID()) == null)
                        {
                            player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.binding"), false);
                        }
                        else
                        {
                            player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.switch"), false);
                        }
                        data.getData().setMailboxData(player.getUUID(), worldIn.dimension(), topPos);
                        MailboxManager.updateState(worldIn, topPos);
                        AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayer) player, new ResourceLocation("contact:root"));
                        return InteractionResult.SUCCESS;
                    }
                }
                if (Objects.equals(mailboxOwner, player.getUUID()))
                {
                    // 获取邮件
                    ItemStackHandler contents = data.getData().getMailboxContents(mailboxOwner);
                    boolean isEmpty = true;
                    for (int i = 0; i < contents.getSlots(); ++i)
                    {
                        ItemStack parcel = contents.getStackInSlot(i);
                        if (!parcel.isEmpty())
                        {
                            if (parcel.getItem() instanceof PostcardItem)
                            {
                                AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayer) player, new ResourceLocation("contact:receive_postcard"));
                            }
                            if (parcel.getOrCreateTag().contains("AnotherLevel"))
                            {
                                AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayer) player, new ResourceLocation("contact:from_another_world"));
                            }

                            player.getInventory().placeItemBackInInventory(parcel);
                            isEmpty = false;
                        }
                    }
                    // 腾空邮件列表
                    data.getData().resetMailboxContents(mailboxOwner);
                    if (!isEmpty)
                    {
                        player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.pick_up"), false);
                    }
                    else
                    {
                        player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.empty"), false);
                    }
                    MailboxManager.updateState(worldIn, topPos);
                    return InteractionResult.SUCCESS;
                }
                else if (player.getItemInHand(handIn).getItem() instanceof IMailItem)
                {
                    if (mailboxOwner != null)
                    {
                        // 不是主人的话，如果有包裹和明信片，那么塞进去
                        if (data.getData().isMailboxEmpty(mailboxOwner))
                        {
                            ItemStack held = player.getItemInHand(handIn).copy();
                            held.getOrCreateTag().putString("Sender", player.getName().getString());
                            data.getData().addMailboxContents(mailboxOwner, held);
                            player.setItemInHand(handIn, ItemStack.EMPTY);
                            player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.deliver"), false);
                            AdvancementManager.givePlayerAdvancement(player.getServer(), (ServerPlayer) player, new ResourceLocation("contact:send_in_person"));
                            MailboxManager.updateState(worldIn, topPos);
                        }
                        else
                        {
                            player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.full"), false);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    else
                    {
                        player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.no_owner"), false);
                    }
                    return InteractionResult.SUCCESS;
                }
                else if (mailboxOwner != null)
                {
                    ServerLifecycleHooks.getCurrentServer().getProfileCache().get(mailboxOwner).ifPresent( gameProfile ->
                            player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.others", gameProfile.getName()), false));
                    return InteractionResult.SUCCESS;
                }
                player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.no_owner_tips"), false);
                return InteractionResult.FAIL;
            }).orElse(InteractionResult.FAIL);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player)
    {
        if (!worldIn.isClientSide)
        {
            if (player.isCreative())
            {
                removeBottomHalf(worldIn, pos, state, player);
            }
            worldIn.getServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
            {
                BlockPos topPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
                data.getData().removeMailboxData(GlobalPos.of(worldIn.dimension(), topPos));
            });
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos blockpos = context.getClickedPos();
        if (blockpos.getY() < 255 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context))
        {
            return super.getStateForPlacement(context).setValue(HALF, DoubleBlockHalf.LOWER);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP) : blockstate.is(this);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType)
    {
        return createTickerHelper(pBlockEntityType, MailboxBlockEntity::tick);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? Lists.newArrayList(new ItemStack(this)) : Collections.emptyList();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return MAILBOX_BLOCK_ENTITY.get().create(pPos, pState);
    }
}
