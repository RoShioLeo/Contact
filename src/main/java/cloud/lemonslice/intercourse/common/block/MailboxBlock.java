package cloud.lemonslice.intercourse.common.block;

import cloud.lemonslice.intercourse.common.handler.AdvancementManager;
import cloud.lemonslice.intercourse.common.handler.MailboxManager;
import cloud.lemonslice.intercourse.common.item.IMailItem;
import cloud.lemonslice.intercourse.common.item.PostcardItem;
import cloud.lemonslice.silveroak.common.block.NormalHorizontalBlock;
import cloud.lemonslice.silveroak.helper.VoxelShapeHelper;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static cloud.lemonslice.intercourse.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;
import static cloud.lemonslice.intercourse.common.tileentity.TileEntityTypeRegistry.MAILBOX;

public class MailboxBlock extends NormalHorizontalBlock
{
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public final DyeColor boxColor;
    public final DyeColor flagColor;
    public static final VoxelShape LOWER_SHAPE;
    public static final VoxelShape UPPER_SHAPE_NORTH;
    public static final VoxelShape UPPER_SHAPE_EAST;

    public MailboxBlock(DyeColor boxColor, DyeColor flagColor)
    {
        super(Block.Properties.create(Material.ROCK, boxColor).notSolid().sound(SoundType.STONE).hardnessAndResistance(1.5F, 6.0F), boxColor.getString() + "_mailbox");
        this.boxColor = boxColor;
        this.flagColor = flagColor;
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.LOWER).with(OPEN, false));
    }

    public MailboxBlock(DyeColor boxColor)
    {
        this(boxColor, DyeColor.RED);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (state.get(HALF).equals(DoubleBlockHalf.LOWER))
        {
            return LOWER_SHAPE;
        }
        else
        {
            switch (state.get(HORIZONTAL_FACING))
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(HALF, OPEN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP))
        {
            return facingState.matchesBlock(this) && facingState.get(HALF) != doubleblockhalf ? stateIn.with(HORIZONTAL_FACING, facingState.get(HORIZONTAL_FACING)) : Blocks.AIR.getDefaultState();
        }
        else
        {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    protected static void removeBottomHalf(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER)
        {
            BlockPos blockpos = pos.down();
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.get(HALF) == DoubleBlockHalf.LOWER)
            {
                world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                world.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isRemote)
        {
            return worldIn.getServer().getWorld(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).map(data ->
            {
                BlockPos topPos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos : pos.up();
                UUID mailboxOwner = data.PLAYERS_DATA.getMailboxOwner(worldIn.getDimensionKey(), topPos);
                if (player.isSneaking())
                {
                    // 检查邮箱主人，没有的话，录入
                    if (mailboxOwner == null)
                    {
                        if (data.PLAYERS_DATA.getMailboxPos(player.getUniqueID()) == null)
                        {
                            player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.binding"), false);
                        }
                        else
                        {
                            player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.switch"), false);
                        }
                        data.PLAYERS_DATA.setMailboxData(player.getUniqueID(), worldIn.getDimensionKey(), topPos);
                        MailboxManager.updateState(worldIn, topPos);
                        AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayerEntity) player, new ResourceLocation("intercourse:root"));
                        return ActionResultType.SUCCESS;
                    }
                }
                if (Objects.equals(mailboxOwner, player.getUniqueID()))
                {
                    // 获取邮件
                    ItemStackHandler contents = data.PLAYERS_DATA.getMailboxContents(mailboxOwner);
                    boolean isEmpty = true;
                    for (int i = 0; i < contents.getSlots(); ++i)
                    {
                        ItemStack parcel = contents.getStackInSlot(i);
                        if (!parcel.isEmpty())
                        {
                            if (parcel.getItem() instanceof PostcardItem)
                            {
                                AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayerEntity) player, new ResourceLocation("intercourse:receive_postcard"));
                            }
                            if (parcel.getOrCreateTag().contains("AnotherWorld"))
                            {
                                AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayerEntity) player, new ResourceLocation("intercourse:from_another_world"));
                            }

                            player.inventory.placeItemBackInInventory(worldIn, parcel);
                            isEmpty = false;
                        }
                    }
                    // 腾空邮件列表
                    data.PLAYERS_DATA.resetMailboxContents(mailboxOwner);
                    if (!isEmpty)
                    {
                        player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.pick_up"), false);
                    }
                    else
                    {
                        player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.empty"), false);
                    }
                    MailboxManager.updateState(worldIn, topPos);
                    return ActionResultType.SUCCESS;
                }
                else if (player.getHeldItem(handIn).getItem() instanceof IMailItem)
                {
                    if (mailboxOwner != null)
                    {
                        // 不是主人的话，如果有包裹和明信片，那么塞进去
                        if (data.PLAYERS_DATA.isMailboxEmpty(mailboxOwner))
                        {
                            ItemStack held = player.getHeldItem(handIn).copy();
                            held.getOrCreateTag().putString("Sender", player.getName().getString());
                            data.PLAYERS_DATA.addMailboxContents(mailboxOwner, held);
                            player.setHeldItem(handIn, ItemStack.EMPTY);
                            player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.deliver"), false);
                            AdvancementManager.givePlayerAdvancement(player.getServer(), (ServerPlayerEntity) player, new ResourceLocation("intercourse:send_in_person"));
                            MailboxManager.updateState(worldIn, topPos);
                            return ActionResultType.SUCCESS;
                        }
                        else
                        {
                            player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.full"), false);
                            return ActionResultType.SUCCESS;
                        }
                    }
                    else
                    {
                        player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.no_owner"), false);
                    }
                    return ActionResultType.SUCCESS;
                }
                else if (mailboxOwner != null)
                {
                    GameProfile gameProfile = ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(mailboxOwner);
                    if (gameProfile != null)
                    {
                        player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.others", gameProfile.getName()), false);
                    }
                    return ActionResultType.SUCCESS;
                }
                player.sendStatusMessage(new TranslationTextComponent("message.intercourse.mailbox.no_owner_tips"), false);
                return ActionResultType.FAIL;
            }).orElse(ActionResultType.FAIL);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!worldIn.isRemote)
        {
            if (player.isCreative())
            {
                removeBottomHalf(worldIn, pos, state, player);
            }
            worldIn.getServer().getWorld(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
            {
                BlockPos topPos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos : pos.up();
                data.PLAYERS_DATA.removeMailboxData(GlobalPos.getPosition(worldIn.getDimensionKey(), topPos));
            });
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos blockpos = context.getPos();
        if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context))
        {
            return super.getStateForPlacement(context).with(HALF, DoubleBlockHalf.LOWER);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return state.get(HALF) == DoubleBlockHalf.LOWER ? blockstate.isSolidSide(worldIn, blockpos, Direction.UP) : blockstate.matchesBlock(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return state.get(HALF) == DoubleBlockHalf.UPPER ? Lists.newArrayList(new ItemStack(this)) : Collections.emptyList();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return state.get(HALF) == DoubleBlockHalf.UPPER;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return MAILBOX.create();
    }

    static
    {
        LOWER_SHAPE = VoxelShapeHelper.createVoxelShape(7, 0, 7, 2, 16, 2);
        UPPER_SHAPE_NORTH = VoxelShapeHelper.createVoxelShape(3, 0, 1, 10, 9, 14);
        UPPER_SHAPE_EAST = VoxelShapeHelper.createVoxelShape(1, 0, 3, 14, 9, 10);
    }
}
