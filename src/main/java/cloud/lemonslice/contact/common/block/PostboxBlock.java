package cloud.lemonslice.contact.common.block;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.common.screenhandler.PostboxScreenHandler;
import cloud.lemonslice.silveroak.common.block.DoubleHorizontalBlock;
import cloud.lemonslice.silveroak.common.inter.ISilveroakEntry;
import cloud.lemonslice.silveroak.helper.VoxelShapeHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class PostboxBlock extends DoubleHorizontalBlock implements ISilveroakEntry
{
    private static final Text CONTAINER_NAME = Text.translatable("container.contact.postbox");
    private final boolean isRed;
    public static final VoxelShape LOWER_SHAPE;
    public static final VoxelShape UPPER_SHAPE;

    public PostboxBlock(boolean isRed)
    {
        super(Settings.create().nonOpaque().sounds(BlockSoundGroup.STONE).strength(1.5F, 6.0F));
        this.isRed = isRed;
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.UPPER));
    }

    @Override
    protected VoxelShape getLowerShape()
    {
        return LOWER_SHAPE;
    }

    @Override
    protected VoxelShape getUpperShape()
    {
        return UPPER_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient())
        {
            player.openHandledScreen(getContainer(isRed));
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    public static NamedScreenHandlerFactory getContainer(boolean isRed)
    {
        return new SimpleNamedScreenHandlerFactory((id, inventory, player) -> new PostboxScreenHandler(id, inventory, isRed), CONTAINER_NAME);
    }

    static
    {
        VoxelShape bottom = VoxelShapeHelper.createVoxelShape(1, 0, 1, 14, 9, 14);
        VoxelShape pillarBottom = VoxelShapeHelper.createVoxelShape(2, 9, 2, 12, 7, 12);
        VoxelShape pillarTop = VoxelShapeHelper.createVoxelShape(2, 0, 2, 12, 11, 12);
        VoxelShape topBottom = VoxelShapeHelper.createVoxelShape(0, 11, 0, 16, 3, 16);
        VoxelShape topTop = VoxelShapeHelper.createVoxelShape(3, 14, 3, 10, 2, 10);
        LOWER_SHAPE = VoxelShapes.union(bottom, pillarBottom);
        UPPER_SHAPE = VoxelShapes.union(pillarTop, topBottom, topTop);
    }

    @Override
    public Identifier getRegistryID()
    {
        return Contact.getIdentifier(isRed ? "red_postbox" : "green_postbox");
    }
}
