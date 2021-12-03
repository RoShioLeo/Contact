package cloud.lemonslice.contact.common.block;

import cloud.lemonslice.contact.common.handler.AdvancementManager;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.item.IMailItem;
import cloud.lemonslice.contact.common.item.PostcardItem;
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

import static cloud.lemonslice.contact.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;
import static cloud.lemonslice.contact.common.tileentity.TileEntityTypeRegistry.MAILBOX;

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
        super(Block.Properties.of(Material.STONE, boxColor).noOcclusion().sound(SoundType.STONE).strength(1.5F, 6.0F), boxColor.getSerializedName() + "_mailbox");
        this.boxColor = boxColor;
        this.flagColor = flagColor;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, OPEN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
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

    protected static void removeBottomHalf(World world, BlockPos pos, BlockState state, PlayerEntity player)
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

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isClientSide)
        {
            return worldIn.getServer().getLevel(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).map(data ->
            {
                BlockPos topPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
                UUID mailboxOwner = data.PLAYERS_DATA.getMailboxOwner(worldIn.dimension(), topPos);
                if (player.isShiftKeyDown())
                {
                    // 检查邮箱主人，没有的话，录入
                    if (mailboxOwner == null)
                    {
                        if (data.PLAYERS_DATA.getMailboxPos(player.getUUID()) == null)
                        {
                            player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.binding"), false);
                        }
                        else
                        {
                            player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.switch"), false);
                        }
                        data.PLAYERS_DATA.setMailboxData(player.getUUID(), worldIn.dimension(), topPos);
                        MailboxManager.updateState(worldIn, topPos);
                        AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayerEntity) player, new ResourceLocation("contact:root"));
                        return ActionResultType.SUCCESS;
                    }
                }
                if (Objects.equals(mailboxOwner, player.getUUID()))
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
                                AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayerEntity) player, new ResourceLocation("contact:receive_postcard"));
                            }
                            if (parcel.getOrCreateTag().contains("AnotherWorld"))
                            {
                                AdvancementManager.givePlayerAdvancement(worldIn.getServer(), (ServerPlayerEntity) player, new ResourceLocation("contact:from_another_world"));
                            }

                            player.inventory.placeItemBackInInventory(worldIn, parcel);
                            isEmpty = false;
                        }
                    }
                    // 腾空邮件列表
                    data.PLAYERS_DATA.resetMailboxContents(mailboxOwner);
                    if (!isEmpty)
                    {
                        player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.pick_up"), false);
                    }
                    else
                    {
                        player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.empty"), false);
                    }
                    MailboxManager.updateState(worldIn, topPos);
                    return ActionResultType.SUCCESS;
                }
                else if (player.getItemInHand(handIn).getItem() instanceof IMailItem)
                {
                    if (mailboxOwner != null)
                    {
                        // 不是主人的话，如果有包裹和明信片，那么塞进去
                        if (data.PLAYERS_DATA.isMailboxEmpty(mailboxOwner))
                        {
                            ItemStack held = player.getItemInHand(handIn).copy();
                            held.getOrCreateTag().putString("Sender", player.getName().getString());
                            data.PLAYERS_DATA.addMailboxContents(mailboxOwner, held);
                            player.setItemInHand(handIn, ItemStack.EMPTY);
                            player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.deliver"), false);
                            AdvancementManager.givePlayerAdvancement(player.getServer(), (ServerPlayerEntity) player, new ResourceLocation("contact:send_in_person"));
                            MailboxManager.updateState(worldIn, topPos);
                            return ActionResultType.SUCCESS;
                        }
                        else
                        {
                            player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.full"), false);
                            return ActionResultType.SUCCESS;
                        }
                    }
                    else
                    {
                        player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.no_owner"), false);
                    }
                    return ActionResultType.SUCCESS;
                }
                else if (mailboxOwner != null)
                {
                    GameProfile gameProfile = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(mailboxOwner);
                    if (gameProfile != null)
                    {
                        player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.others", gameProfile.getName()), false);
                    }
                    return ActionResultType.SUCCESS;
                }
                player.displayClientMessage(new TranslationTextComponent("message.contact.mailbox.no_owner_tips"), false);
                return ActionResultType.FAIL;
            }).orElse(ActionResultType.FAIL);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!worldIn.isClientSide)
        {
            if (player.isCreative())
            {
                removeBottomHalf(worldIn, pos, state, player);
            }
            worldIn.getServer().getLevel(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
            {
                BlockPos topPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
                data.PLAYERS_DATA.removeMailboxData(GlobalPos.of(worldIn.dimension(), topPos));
            });
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
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
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP) : blockstate.is(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? Lists.newArrayList(new ItemStack(this)) : Collections.emptyList();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER;
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
