package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.client.widget.EditableTextBox;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PostcardEditGui extends Screen
{
    private final PostcardStyle style;
    private final ItemStack postcard;
    private final Player editingPlayer;
    private final InteractionHand hand;
    private EditableTextBox textBox;
    private Button buttonDone;

    public PostcardEditGui(ItemStack postcardIn, Player playerIn, InteractionHand handIn)
    {
        super(TextComponent.EMPTY);
        this.postcard = postcardIn;
        this.editingPlayer = playerIn;
        this.hand = handIn;
        CompoundTag tag = postcardIn.getTag();
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
        this.textBox = this.addWidget(new EditableTextBox(postcard, editingPlayer, hand, (this.width - style.cardWidth) / 2 + style.textPosX, (this.height / 2 - style.cardHeight * 2 / 3) + style.textPosY, style.textWidth, style.textHeight, 12, style.textColor, new TextComponent("Postcard")));
    }

    @Override
    protected void init()
    {
        this.buttonDone = this.addRenderableWidget(new Button(this.width / 2 - 48, this.height / 2 + style.cardHeight / 3 + 20, 98, 20, CommonComponents.GUI_DONE, (button) ->
        {
            this.minecraft.setScreen(null);
            textBox.sendTextToServer();
        }));

        this.textBox.init();
        this.textBox.x = (this.width - style.cardWidth) / 2 + style.textPosX;
        this.textBox.y = (this.height / 2 - style.cardHeight * 2 / 3) + style.textPosY;
        this.textBox.shouldRefresh();

        this.setInitialFocus(textBox);
    }

    @Override
    public void tick()
    {
        super.tick();
        textBox.tick();
    }

    @Override
    public void removed()
    {
        textBox.onClose();
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, style.getCardTexture());
        blit(poseStack, (this.width - style.cardWidth) / 2, this.height / 2 - style.cardHeight * 2 / 3, style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        textBox.render(poseStack, mouseX, mouseY, partialTicks);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
