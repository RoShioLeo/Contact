package cloud.lemonslice.contact.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public interface IMailItem
{
    boolean isEnderType();

    default void addSenderInfoTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        if (stack.getOrCreateTag().contains("Sender"))
        {
            tooltip.add(Component.translatable("tooltip.contact.mail.sender", stack.getOrCreateTag().getString("Sender")).withStyle(ChatFormatting.GRAY));
        }
    }


}
