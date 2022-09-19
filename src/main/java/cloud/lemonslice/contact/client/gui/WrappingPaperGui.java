package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.common.container.WrappingPaperContainer;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import cloud.lemonslice.silveroak.client.widget.IconButton;
import cloud.lemonslice.silveroak.helper.GuiHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static cloud.lemonslice.contact.Contact.MODID;

public class WrappingPaperGui extends AbstractContainerScreen<WrappingPaperContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/wrapping_paper.png");
    private int offsetX;
    private int offsetY;
    private IconButton buttonPack;

    public WrappingPaperGui(WrappingPaperContainer screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.imageHeight = 133;
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - this.imageWidth) / 2;
        this.offsetY = (this.height - this.imageHeight) / 2;

        this.buttonPack = addWidget(new IconButton(offsetX + 124, offsetY + 15, 18, 19, Component.translatable("tooltip.contact.wrapping_paper.pack"), button -> pack(), this::buttonTooltip));
    }

    private void buttonTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY)
    {
        if (button.isHoveredOrFocused())
        {
            GuiHelper.drawTooltip(this, poseStack, mouseX, mouseY, button.x, button.y, 18, 19, Lists.newArrayList(button.getMessage()));
        }
    }

    private void pack()
    {
        SimpleNetworkHandler.CHANNEL.sendToServer(new ActionMessage(0));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, TEXTURE);
        GuiHelper.drawLayer(poseStack, offsetX, offsetY, new TexturePos(0, 0, imageWidth, imageHeight));

        GuiHelper.renderButton(poseStack, partialTicks, x, y, this.getBlitOffset(), TEXTURE, buttonPack,
                new TexturePos(imageWidth, 0, 18, 19),
                new TexturePos(imageWidth, 19, 18, 19));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
    }
}
