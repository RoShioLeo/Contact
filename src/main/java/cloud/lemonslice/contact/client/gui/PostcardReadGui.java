package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.client.widget.ReadOnlyTextBox;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

public class PostcardReadGui extends Screen
{
    private final PostcardStyle style;
    private final ItemStack postcard;
    private ReadOnlyTextBox textBox;

    public PostcardReadGui(ItemStack postcardIn)
    {
        super(NarratorChatListener.NO_TITLE);
        this.postcard = postcardIn;
        CompoundNBT compoundnbt = postcardIn.getTag();
        if (compoundnbt != null)
        {
            if (compoundnbt.contains("Info"))
            {
                style = PostcardStyle.fromNBT(compoundnbt);
            }
            else if (compoundnbt.contains("CardID"))
            {
                style = PostcardStyle.fromNBT(compoundnbt);
            }
            else style = PostcardStyle.DEFAULT;
        }
        else style = PostcardStyle.DEFAULT;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        InputMappings.Input mouseKey = InputMappings.getKey(keyCode, scanCode);
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
        this.textBox = new ReadOnlyTextBox(postcard, (this.width - style.cardWidth) / 2 + style.textPosX, style.textPosY + (this.height - style.cardHeight) / 2, style.textWidth, style.textHeight, 12, style.textColor, new StringTextComponent("Postcard"));
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.setFocused(null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(style.getCardTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2, (this.height - style.cardHeight) / 2, style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        RenderSystem.enableBlend();
        RenderSystem.color4f(ColorHelper.getRedF(style.postmarkColor), ColorHelper.getGreenF(style.postmarkColor), ColorHelper.getBlueF(style.postmarkColor), ColorHelper.getAlphaF(style.postmarkColor));

        minecraft.getTextureManager().bind(style.getPostmarkTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2 + style.postmarkPosX, (this.height - style.cardHeight) / 2 + style.postmarkPosY, style.postmarkWidth, style.postmarkHeight, 0, 0, style.postmarkWidth, style.postmarkHeight, style.postmarkWidth, style.postmarkHeight);
        RenderSystem.disableBlend();

        textBox.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
