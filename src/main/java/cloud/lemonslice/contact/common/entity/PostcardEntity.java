package cloud.lemonslice.contact.common.entity;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import static cloud.lemonslice.contact.common.entity.EntityTypeRegistry.POSTCARD;

public class PostcardEntity extends AbstractDecorationEntity
{
    private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(PostcardEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> ROTATION = DataTracker.registerData(PostcardEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private boolean fixed;

    public PostcardEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public PostcardEntity(World world, BlockPos pos, Direction facing)
    {
        this(POSTCARD, world, pos, facing);
    }

    public PostcardEntity(EntityType<? extends AbstractDecorationEntity> type, World world, BlockPos pos, Direction facing)
    {
        super(type, world, pos);
        this.setFacing(facing);
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions)
    {
        return 0.0f;
    }

    @Override
    protected void initDataTracker()
    {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
        this.getDataTracker().startTracking(ROTATION, 0);
    }

    @Override
    protected void setFacing(Direction facing)
    {
        Validate.notNull(facing);
        this.facing = facing;
        if (facing.getAxis().isHorizontal())
        {
            this.setPitch(0.0f);
            this.setYaw(this.facing.getHorizontal() * 90);
        }
        else
        {
            this.setPitch(-90 * facing.getDirection().offset());
            this.setYaw(0.0f);
        }
        this.prevPitch = this.getPitch();
        this.prevYaw = this.getYaw();
        this.updateAttachmentPosition();
    }

    @Override
    protected void updateAttachmentPosition()
    {
        if (this.facing == null)
        {
            return;
        }
        double e = (double) this.attachmentPos.getX() + 0.5 - (double) this.facing.getOffsetX() * 0.46875;
        double f = (double) this.attachmentPos.getY() + 0.5 - (double) this.facing.getOffsetY() * 0.46875;
        double g = (double) this.attachmentPos.getZ() + 0.5 - (double) this.facing.getOffsetZ() * 0.46875;
        this.setPos(e, f, g);
        double h = this.getWidthPixels();
        double i = this.getHeightPixels();
        double j = this.getWidthPixels();
        Direction.Axis axis = this.facing.getAxis();
        switch (axis)
        {
            case X -> h = 1.0;
            case Y -> i = 1.0;
            case Z -> j = 1.0;
        }
        this.setBoundingBox(new Box(e - (h /= 32.0), f - (i /= 32.0), g - (j /= 32.0), e + h, f + i, g + j));
    }

    @Override
    public boolean canStayAttached()
    {
        if (this.fixed)
        {
            return true;
        }
        if (!this.world.isSpaceEmpty(this))
        {
            return false;
        }
        BlockState blockState = this.world.getBlockState(this.attachmentPos.offset(this.facing.getOpposite()));
        if (!(blockState.getMaterial().isSolid() || this.facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState)))
        {
            return false;
        }
        return this.world.getOtherEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
    }

    @Override
    public void move(MovementType movementType, Vec3d movement)
    {
        if (!this.fixed)
        {
            super.move(movementType, movement);
        }
    }

    @Override
    public void addVelocity(double deltaX, double deltaY, double deltaZ)
    {
        if (!this.fixed)
        {
            super.addVelocity(deltaX, deltaY, deltaZ);
        }
    }

    @Override
    public float getTargetingMargin()
    {
        return 0.0f;
    }

    @Override
    public boolean damage(DamageSource source, float amount)
    {
        if (this.fixed)
        {
            if (source == DamageSource.OUT_OF_WORLD || source.isSourceCreativePlayer())
            {
                return super.damage(source, amount);
            }
            return false;
        }
        if (this.isInvulnerableTo(source))
        {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public int getWidthPixels()
    {
        return 12;
    }

    @Override
    public int getHeightPixels()
    {
        return 12;
    }

    @Override
    public boolean shouldRender(double distance)
    {
        double d = 16.0;
        return distance < (d *= 64.0 * ItemFrameEntity.getRenderDistanceMultiplier()) * d;
    }

    @Override
    public void onBreak(@Nullable Entity entity)
    {
        this.playSound(this.getBreakSound(), 1.0f, 1.0f);
        this.dropPostcard(entity);
    }

    public SoundEvent getBreakSound()
    {
        return SoundEvents.ENTITY_ITEM_FRAME_BREAK;
    }

    @Override
    public void onPlace()
    {
        this.playSound(this.getPlaceSound(), 1.0f, 1.0f);
    }

    public SoundEvent getPlaceSound()
    {
        return SoundEvents.ENTITY_ITEM_FRAME_PLACE;
    }

    private void dropPostcard(@Nullable Entity entity)
    {
        if (this.fixed)
        {
            return;
        }
        ItemStack postcard = this.getPostcard();
        this.setHeldItemStack(ItemStack.EMPTY);
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
        {
            return;
        }
        if (entity instanceof PlayerEntity playerEntity)
        {
            if (playerEntity.getAbilities().creativeMode)
            {
                return;
            }
        }
        if (!postcard.isEmpty())
        {
            postcard = postcard.copy();
            this.dropStack(postcard);
        }
    }

    public ItemStack getPostcard()
    {
        return this.getDataTracker().get(ITEM_STACK);
    }

    public void setHeldItemStack(ItemStack value)
    {
        if (!value.isEmpty())
        {
            value = value.copy();
            value.setCount(1);
        }
        this.setAsStackHolder(value);
        this.getDataTracker().set(ITEM_STACK, value);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data)
    {
        if (data.equals(ITEM_STACK))
        {
            this.setAsStackHolder(this.getPostcard());
        }
    }

    private void setAsStackHolder(ItemStack stack)
    {
        if (!stack.isEmpty() && stack.getHolder() != this)
        {
            stack.setHolder(this);
        }
        this.updateAttachmentPosition();
    }

    public int getRotation()
    {
        return this.getDataTracker().get(ROTATION);
    }

    private void setRotation(int value)
    {
        this.getDataTracker().set(ROTATION, value % 16);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        if (!this.getPostcard().isEmpty())
        {
            nbt.put("Item", this.getPostcard().writeNbt(new NbtCompound()));
            nbt.putByte("ItemRotation", (byte) this.getRotation());
        }
        nbt.putByte("Facing", (byte) this.facing.getId());
        nbt.putBoolean("Invisible", this.isInvisible());
        nbt.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        NbtCompound nbtCompound = nbt.getCompound("Item");
        if (nbtCompound != null && !nbtCompound.isEmpty())
        {
            ItemStack postcard = ItemStack.fromNbt(nbtCompound);
            this.setHeldItemStack(postcard);
            this.setRotation(nbt.getByte("ItemRotation"));
        }
        this.setFacing(Direction.byId(nbt.getByte("Facing")));
        this.setInvisible(nbt.getBoolean("Invisible"));
        this.fixed = nbt.getBoolean("Fixed");
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        boolean isEmpty = !this.getPostcard().isEmpty();
        if (this.fixed)
        {
            return ActionResult.PASS;
        }
        if (this.world.isClient)
        {
            return isEmpty ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        else if (!isEmpty)
        {
            this.damage(DamageSource.player(player), 1);
        }
        this.playSound(this.getRotateItemSound(), 1.0f, 1.0f);
        if (getHorizontalFacing().getId() > 1)
        {
            this.setRotation((this.getRotation() + 1) % 3);
        }
        else
        {
            this.setRotation(this.getRotation() + 1);
        }

        return ActionResult.CONSUME;
    }

    public SoundEvent getRotateItemSound()
    {
        return SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet)
    {
        super.onSpawnPacket(packet);
        this.setFacing(Direction.byId(packet.getEntityData()));
    }

    @Override
    public ItemStack getPickBlockStack()
    {
        return this.getPostcard();
    }

    @Override
    public float getBodyYaw()
    {
        Direction direction = this.getHorizontalFacing();
        int i = direction.getAxis().isVertical() ? 90 * direction.getDirection().offset() : 0;
        return MathHelper.wrapDegrees(180 + direction.getHorizontal() * 90 + this.getRotation() * 45 + i);
    }
}
