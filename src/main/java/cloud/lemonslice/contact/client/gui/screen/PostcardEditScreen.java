package cloud.lemonslice.contact.client.gui.screen;

import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.client.widget.EditableTextBox;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class PostcardEditScreen extends Screen
{
    private final PostcardStyle style;
    private EditableTextBox textBox;
    private ButtonWidget buttonDone;
    private final ItemStack postcard;
    private final PlayerEntity editingPlayer;
    private final Hand hand;

    public PostcardEditScreen(ItemStack postcardIn, PlayerEntity playerIn, Hand handIn)
    {
        super(Text.empty());
        this.postcard = postcardIn;
        this.editingPlayer = playerIn;
        this.hand = handIn;
        NbtCompound tag = postcardIn.getNbt();
        if (tag != null)
        {
            if (tag.contains("Info"))
            {
                style = PostcardStyle.fromNBT(tag);
            }
            else if (tag.contains("CardID"))
            {
                style = PostcardStyle.fromNBT(tag);
            }
            else style = PostcardStyle.DEFAULT;
        }
        else style = PostcardStyle.DEFAULT;
        this.textBox = this.addDrawable(new EditableTextBox(postcard, editingPlayer, hand, (this.width - style.cardWidth) / 2 + style.textPosX, (this.height / 2 - style.cardHeight * 2 / 3) + style.textPosY, style.textWidth, style.textHeight, 12, style.textColor, Text.literal("Postcard")));
    }

    @Override
    protected void init()
    {
        this.buttonDone = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) ->
                {
                    this.client.setScreen(null);
                    textBox.sendTextToServer();
                })
                .position(this.width / 2 - 48, this.height / 2 + style.cardHeight / 3 + 20)
                .size(98, 20)
                .build());

        this.textBox.setX((this.width - style.cardWidth) / 2 + style.textPosX);
        this.textBox.setY((this.height / 2 - style.cardHeight * 2 / 3) + style.textPosY);
        this.textBox.shouldRefresh();

        this.setInitialFocus(textBox);
    }

    @Override
    public void tick()
    {
        super.tick();
        textBox.tick();
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if (!super.mouseClicked(pMouseX, pMouseY, pButton))
        {
            textBox.mouseClicked(pMouseX, pMouseY, pButton);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
    {
        if (!super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY))
        {
            textBox.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, style.getCardTexture());
        drawTexture(matrices, (this.width - style.cardWidth) / 2, this.height / 2 - style.cardHeight * 2 / 3, style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        textBox.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }
}
