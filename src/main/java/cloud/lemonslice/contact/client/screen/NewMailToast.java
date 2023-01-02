package cloud.lemonslice.contact.client.screen;

import cloud.lemonslice.contact.common.item.ItemRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class NewMailToast implements Toast
{
    @Override
    public Visibility draw(MatrixStack matrixStack, ToastManager manager, long ticks)
    {
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        manager.drawTexture(matrixStack, 0, 0, 0, 0, this.getWidth(), this.getHeight());

        List<OrderedText> list = manager.getClient().textRenderer.wrapLines(Text.translatable("info.contact.new_mail.desc"), 125);
        int i = 16776960;
        if (list.size() == 1)
        {
            manager.getClient().textRenderer.draw(matrixStack, Text.translatable("info.contact.new_mail.title"), 30.0F, 7.0F, i | -16777216);
            manager.getClient().textRenderer.draw(matrixStack, list.get(0), 30.0F, 18.0F, -1);
        }
        else
        {
            if (ticks < 1500L)
            {
                int k = MathHelper.floor(MathHelper.clamp((float) (1500L - ticks) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                manager.getClient().textRenderer.draw(matrixStack, Text.translatable("info.contact.new_mail.desc"), 30.0F, 11.0F, i | k);
            }
            else
            {
                int i1 = MathHelper.floor(MathHelper.clamp((float) (ticks - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                int l = this.getHeight() / 2 - list.size() * 9 / 2;

                for (OrderedText orderedText : list)
                {
                    manager.getClient().textRenderer.draw(matrixStack, orderedText, 30.0F, (float) l, 16777215 | i1);
                    l += 9;
                }
            }
        }

        manager.getClient().getItemRenderer().renderGuiItemIcon(new ItemStack(ItemRegistry.MAIL), 8, 8);
        return ticks >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
