package cloud.lemonslice.contact.client.gui.screen;

import cloud.lemonslice.contact.common.item.ItemRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class NewMailToast implements Toast
{
    @Override
    public Visibility draw(DrawContext drawContext, ToastManager manager, long ticks)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawContext.drawTexture(TEXTURE, 0, 0, 0, 0, this.getWidth(), this.getHeight());

        List<OrderedText> list = manager.getClient().textRenderer.wrapLines(Text.translatable("info.contact.new_mail.desc"), 125);
        int i = 16776960;
        if (list.size() == 1)
        {
            drawContext.drawText(manager.getClient().textRenderer, Text.translatable("info.contact.new_mail.title"), 30, 7, i | -16777216, false);
            drawContext.drawText(manager.getClient().textRenderer, list.get(0), 30, 18, -1, false);
        }
        else
        {
            if (ticks < 1500L)
            {
                int k = MathHelper.floor(MathHelper.clamp((float) (1500L - ticks) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                drawContext.drawText(manager.getClient().textRenderer, Text.translatable("info.contact.new_mail.desc"), 30, 11, i | k, false);
            }
            else
            {
                int i1 = MathHelper.floor(MathHelper.clamp((float) (ticks - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                int l = this.getHeight() / 2 - list.size() * 9 / 2;

                for (OrderedText orderedText : list)
                {
                    drawContext.drawText(manager.getClient().textRenderer, orderedText, 30, l, 16777215 | i1, false);
                    l += 9;
                }
            }
        }

        drawContext.drawItem(new ItemStack(ItemRegistry.LETTER), 8, 8);
        return ticks >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
