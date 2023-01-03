package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PostcardItem extends NormalItem implements IMailItem
{
    private final boolean isEnderType;

    public PostcardItem(boolean isEnderType)
    {
        super(new Item.Properties().stacksTo(1));
        this.isEnderType = isEnderType;
    }

//    @Override
//    public boolean hasEffect(ItemStack stack)
//    {
//        return stack.getOrCreateTag().contains("Sender");
//    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide)
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
        playerIn.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        if (stack.getOrCreateTag().contains("Info"))
        {
            MutableComponent background = Component.translatable("tooltip.contact.postcard." + stack.getOrCreateTag().getCompound("Info").getString("ID")).withStyle(ChatFormatting.GRAY);
            tooltip.add(Component.translatable("tooltip.contact.postcard.background", background).withStyle(ChatFormatting.GRAY));
        }
        if (stack.getOrCreateTag().contains("CardID"))
        {
            ResourceLocation id = new ResourceLocation(stack.getOrCreateTag().getString("CardID"));
            MutableComponent background = Component.translatable("tooltip.postcard." + id.getNamespace() + "." + id.getPath()).withStyle(ChatFormatting.GRAY);
            tooltip.add(Component.translatable("tooltip.contact.postcard.background", background).withStyle(ChatFormatting.GRAY));
        }
        this.addSenderInfoTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isEnderType()
    {
        return isEnderType;
    }

    public static ItemStack getPostcard(ResourceLocation id, boolean isEnderType)
    {
        ItemStack postcard = new ItemStack(isEnderType ? ItemRegistry.ENDER_POSTCARD.get() : ItemRegistry.POSTCARD.get());
        CompoundTag nbt = new CompoundTag();
        nbt.putString("CardID", id.toString());
        postcard.setTag(nbt);
        return postcard;
    }

    public static ItemStack setText(ItemStack postcard, String text)
    {
        postcard.addTagElement("Text", StringTag.valueOf(text));
        return postcard;
    }
}
