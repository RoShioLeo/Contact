package cloud.lemonslice.intercourse.client.gui;

import cloud.lemonslice.intercourse.common.item.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class NewMailToast implements IToast
{
    @Override
    public Visibility func_230444_a_(MatrixStack matrixStack, ToastGui gui, long ticks)
    {
        gui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        gui.blit(matrixStack, 0, 0, 0, 0, this.func_230445_a_(), this.func_238540_d_());

        List<IReorderingProcessor> list = gui.getMinecraft().fontRenderer.trimStringToWidth(new TranslationTextComponent("info.intercourse.new_mail.desc"), 125);
        int i = 16776960;
        if (list.size() == 1)
        {
            gui.getMinecraft().fontRenderer.drawText(matrixStack, new TranslationTextComponent("info.intercourse.new_mail.title"), 30.0F, 7.0F, i | -16777216);
            gui.getMinecraft().fontRenderer.func_238422_b_(matrixStack, list.get(0), 30.0F, 18.0F, -1);
        }
        else
        {
            if (ticks < 1500L)
            {
                int k = MathHelper.floor(MathHelper.clamp((float) (1500L - ticks) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                gui.getMinecraft().fontRenderer.drawText(matrixStack, new TranslationTextComponent("info.intercourse.new_mail.desc"), 30.0F, 11.0F, i | k);
            }
            else
            {
                int i1 = MathHelper.floor(MathHelper.clamp((float) (ticks - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                int l = this.func_238540_d_() / 2 - list.size() * 9 / 2;

                for (IReorderingProcessor ireorderingprocessor : list)
                {
                    gui.getMinecraft().fontRenderer.func_238422_b_(matrixStack, ireorderingprocessor, 30.0F, (float) l, 16777215 | i1);
                    l += 9;
                }
            }
        }

        gui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(new ItemStack(ItemsRegistry.MAIL), 8, 8);
        return ticks >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }
}
