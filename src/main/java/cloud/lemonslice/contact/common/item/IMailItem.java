package cloud.lemonslice.contact.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface IMailItem
{
    boolean isEnderType();

    default void addSenderInfoTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if (stack.getOrCreateTag().contains("Sender"))
        {
            tooltip.add(new TranslationTextComponent("tooltip.contact.mail.sender", stack.getOrCreateTag().getString("Sender")).withStyle(TextFormatting.GRAY));
        }
    }


}
