package cloud.lemonslice.contact.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IMailItem
{
    boolean isEnderType();

    default void addSenderInfoTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext context)
    {
        if (stack.getOrCreateNbt().contains("Sender"))
        {
            tooltip.add(Text.translatable("tooltip.contact.mail.sender", stack.getOrCreateNbt().getString("Sender")).formatted(Formatting.GRAY));
        }
    }
}
