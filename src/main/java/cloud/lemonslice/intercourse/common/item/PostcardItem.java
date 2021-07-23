package cloud.lemonslice.intercourse.common.item;

import cloud.lemonslice.intercourse.Intercourse;
import cloud.lemonslice.intercourse.client.ClientProxy;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PostcardItem extends NormalItem implements IMailItem
{
    public static final List<Style> STYLES = Lists.newArrayList();
    private final boolean isEnderType;

    public PostcardItem(String name, boolean isEnderType)
    {
        super(name, NormalItem.getNormalItemProperties(Intercourse.ITEM_GROUP).maxStackSize(1));
        this.isEnderType = isEnderType;
    }

//    @Override
//    public boolean hasEffect(ItemStack stack)
//    {
//        return stack.getOrCreateTag().contains("Sender");
//    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote)
        {
            if (itemstack.getOrCreateTag().contains("Sender"))
            {
                ClientProxy.openPostcardToRead(itemstack);
            }
            else
            {
                ClientProxy.openPostcardToEdit(itemstack, playerIn, handIn);
            }
        }
        playerIn.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            for (Style style : STYLES)
            {
                items.add(getPostcard(style, isEnderType()));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if (stack.getOrCreateTag().contains("Info"))
        {
            IFormattableTextComponent background = new TranslationTextComponent("tooltip.intercourse.postcard." + stack.getOrCreateTag().getCompound("Info").getString("ID")).mergeStyle(TextFormatting.GRAY);
            tooltip.add(new TranslationTextComponent("tooltip.intercourse.postcard.background", background).mergeStyle(TextFormatting.GRAY));
        }
        this.addSenderInfoTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isEnderType()
    {
        return isEnderType;
    }

    public static ItemStack getPostcard(Style style, boolean isEnderType)
    {
        ItemStack postcard = new ItemStack(isEnderType ? ItemRegistry.ENDER_POSTCARD : ItemRegistry.POSTCARD);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("ID", style.id);
        nbt.putInt("PosX", style.textPosX);
        nbt.putInt("PosY", style.textPosY);
        nbt.putInt("Width", style.textWidth);
        nbt.putInt("Height", style.textHeight);
        nbt.putInt("Color", style.color);
        postcard.getOrCreateTag().put("Info", nbt);
        return postcard;
    }

    public static class Style
    {
        public final String id;
        public final int textPosX;
        public final int textPosY;
        public final int textWidth;
        public final int textHeight;
        public final int color;

        public Style(String id, int posX, int posY, int textWidth, int textHeight, int color)
        {
            this.id = id;
            this.textPosX = posX;
            this.textPosY = posY;
            this.textWidth = textWidth;
            this.textHeight = textHeight;
            this.color = color;
        }
    }

    static
    {
        STYLES.add(new Style("stripes", 10, 12, 180, 108, 0xff4d4d4d));
        STYLES.add(new Style("moonlit_night", 10, 10, 180, 96, 0xffbcc2d4));
        STYLES.add(new Style("creeper", 10, 12, 180, 108, 0xff1a8047));
        STYLES.add(new Style("spring_day", 10, 12, 180, 96, 0xff52b8cc));
        STYLES.add(new Style("summer_night", 10, 12, 180, 96, 0xffbcc2d4));
        STYLES.add(new Style("autumn_dusk", 10, 12, 180, 96, 0xff4d3f36));
        STYLES.add(new Style("winter_day", 10, 12, 180, 96, 0xff8a9699));
    }
}
