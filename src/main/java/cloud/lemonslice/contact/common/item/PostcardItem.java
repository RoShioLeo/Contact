package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static cloud.lemonslice.contact.Contact.MODID;

public class PostcardItem extends NormalItem implements IMailItem
{
    private final boolean isEnderType;

    public PostcardItem(String id, boolean isEnderType)
    {
        super(new Identifier(MODID, id), new FabricItemSettings().maxCount(1), Contact.ITEM_GROUP);
        this.isEnderType = isEnderType;
    }

//    @Override
//    public boolean hasEffect(ItemStack stack)
//    {
//        return stack.getOrCreateTag().contains("Sender");
//    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemstack = user.getStackInHand(hand);
        if (world.isClient())
        {
            if (itemstack.getOrCreateNbt().contains("Sender"))
            {
                ClientProxy.openPostcardToRead(itemstack);
            }
            else
            {
                ClientProxy.openPostcardToEdit(itemstack, user, hand);
            }
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemstack, world.isClient());
    }

//    @Override
//    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
//    {
//        if (this.allowedIn(group))
//        {
//            for (ResourceLocation id : PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet())
//            {
//                items.add(getPostcard(id, isEnderType()));
//            }
//        }
//    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if (stack.getOrCreateNbt().contains("Info"))
        {
            MutableText background = Text.translatable("tooltip.contact.postcard." + stack.getOrCreateNbt().getCompound("Info").getString("ID")).formatted(Formatting.GRAY);
            tooltip.add(Text.translatable("tooltip.contact.postcard.background", background).formatted(Formatting.GRAY));
        }
        if (stack.getOrCreateNbt().contains("CardID"))
        {
            Identifier id = new Identifier(stack.getOrCreateNbt().getString("CardID"));
            MutableText background = Text.translatable("tooltip.postcard." + id.getNamespace() + "." + id.getPath()).formatted(Formatting.GRAY);
            tooltip.add(Text.translatable("tooltip.contact.postcard.background", background).formatted(Formatting.GRAY));
        }
        this.addSenderInfoTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean isEnderType()
    {
        return isEnderType;
    }

    public static ItemStack getPostcard(Identifier id, boolean isEnderType)
    {
        ItemStack postcard = new ItemStack(isEnderType ? ItemRegistry.ENDER_POSTCARD : ItemRegistry.POSTCARD);
        NbtCompound nbt = new NbtCompound();
        nbt.putString("CardID", id.toString());
        postcard.setNbt(nbt);
        return postcard;
    }

    public static ItemStack setText(ItemStack postcard, String text)
    {
        postcard.setSubNbt("Text", NbtString.of(text));
        return postcard;
    }
}
