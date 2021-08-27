package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PostcardItem extends NormalItem implements IMailItem
{
    private final boolean isEnderType;

    public PostcardItem(String name, boolean isEnderType)
    {
        super(name, NormalItem.getNormalItemProperties(Contact.ITEM_GROUP).maxStackSize(1));
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
            for (ResourceLocation id : PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet())
            {
                items.add(getPostcard(id, isEnderType()));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if (stack.getOrCreateTag().contains("Info"))
        {
            IFormattableTextComponent background = new TranslationTextComponent("tooltip.contact.postcard." + stack.getOrCreateTag().getCompound("Info").getString("ID")).mergeStyle(TextFormatting.GRAY);
            tooltip.add(new TranslationTextComponent("tooltip.contact.postcard.background", background).mergeStyle(TextFormatting.GRAY));
        }
        if (stack.getOrCreateTag().contains("CardID"))
        {
            ResourceLocation id = new ResourceLocation(stack.getOrCreateTag().getString("CardID"));
            IFormattableTextComponent background = new TranslationTextComponent("tooltip.postcard." + id.getNamespace() + "." + id.getPath()).mergeStyle(TextFormatting.GRAY);
            tooltip.add(new TranslationTextComponent("tooltip.contact.postcard.background", background).mergeStyle(TextFormatting.GRAY));
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
        ItemStack postcard = new ItemStack(isEnderType ? ItemRegistry.ENDER_POSTCARD : ItemRegistry.POSTCARD);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("CardID", id.toString());
        postcard.setTag(nbt);
        return postcard;
    }

    public static ItemStack setText(ItemStack postcard, String text)
    {
        postcard.setTagInfo("Text", StringNBT.valueOf(text));
        return postcard;
    }
}
