package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.client.widget.ReadOnlyTextBox;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class PostcardReadGui extends Screen
{
    private final PostcardStyle style;
    private final ItemStack postcard;
    private ReadOnlyTextBox textBox;
    private Button buttonDone;

    public PostcardReadGui(ItemStack postcardIn)
    {
        super(GameNarrator.NO_TITLE);
        this.postcard = postcardIn;
        CompoundTag compoundTag = postcardIn.getTag();
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey))
        {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    protected void init()
    {
        this.textBox = new ReadOnlyTextBox(postcard, (this.width - style.cardWidth) / 2 + style.textPosX, style.textPosY + (this.height / 2 - style.cardHeight * 2 / 3), style.textWidth, style.textHeight, 12, style.textColor, Component.literal("Postcard"));
        this.buttonDone = this.addRenderableWidget(Button
                .builder(CommonComponents.GUI_DONE, (button) -> this.minecraft.setScreen(null))
                .pos(this.width / 2 - 48, this.height / 2 + style.cardHeight / 3 + 20)
                .size(98, 20)
                .build());
    }


    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.setFocused(null);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, style.getCardTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2, (this.height / 2 - style.cardHeight * 2 / 3), style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(ColorHelper.getRedF(style.postmarkColor), ColorHelper.getGreenF(style.postmarkColor), ColorHelper.getBlueF(style.postmarkColor), ColorHelper.getAlphaF(style.postmarkColor));

        RenderSystem.setShaderTexture(0, style.getPostmarkTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2 + style.postmarkPosX, (this.height / 2 - style.cardHeight * 2 / 3) + style.postmarkPosY, style.postmarkWidth, style.postmarkHeight, 0, 0, style.postmarkWidth, style.postmarkHeight, style.postmarkWidth, style.postmarkHeight);
        RenderSystem.disableBlend();

        textBox.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
