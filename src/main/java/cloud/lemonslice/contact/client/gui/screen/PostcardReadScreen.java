package cloud.lemonslice.contact.client.gui.screen;

import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.client.widget.ReadOnlyTextBox;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class PostcardReadScreen extends Screen
{
    private final PostcardStyle style;
    private final ItemStack postcard;
    private ReadOnlyTextBox textBox;
    private ButtonWidget buttonDone;

    public PostcardReadScreen(ItemStack postcardIn)
    {
        super(Text.empty());
        this.postcard = postcardIn;
        NbtCompound compoundTag = postcardIn.getOrCreateNbt();
        if (compoundTag != null)
        {
            if (compoundTag.contains("Info"))
            {
                style = PostcardStyle.fromNBT(compoundTag);
            }
            else if (compoundTag.contains("CardID"))
            {
                style = PostcardStyle.fromNBT(compoundTag);
            }
            else style = PostcardStyle.DEFAULT;
        }
        else style = PostcardStyle.DEFAULT;
    }

    @Override
    protected void init()
    {
        this.textBox = new ReadOnlyTextBox(postcard, (this.width - style.cardWidth) / 2 + style.textPosX, style.textPosY + (this.height / 2 - style.cardHeight * 2 / 3), style.textWidth, style.textHeight, 12, style.textColor, Text.literal("Postcard"));
        this.buttonDone = this.addDrawableChild(ButtonWidget
                .builder(ScreenTexts.DONE, (button) -> this.client.setScreen(null))
                .position(this.width / 2 - 48, this.height / 2 + style.cardHeight / 3 + 20)
                .size(98, 20)
                .build());
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.setFocused(null);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, style.getCardTexture());
        drawTexture(matrixStack, (this.width - style.cardWidth) / 2, (this.height / 2 - style.cardHeight * 2 / 3), style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(ColorHelper.getRedF(style.postmarkColor), ColorHelper.getGreenF(style.postmarkColor), ColorHelper.getBlueF(style.postmarkColor), ColorHelper.getAlphaF(style.postmarkColor));

        RenderSystem.setShaderTexture(0, style.getPostmarkTexture());
        drawTexture(matrixStack, (this.width - style.cardWidth) / 2 + style.postmarkPosX, (this.height / 2 - style.cardHeight * 2 / 3) + style.postmarkPosY, style.postmarkWidth, style.postmarkHeight, 0, 0, style.postmarkWidth, style.postmarkHeight, style.postmarkWidth, style.postmarkHeight);
        RenderSystem.disableBlend();

        textBox.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
