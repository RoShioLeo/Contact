package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.common.container.WrappingPaperContainer;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import cloud.lemonslice.silveroak.client.widget.IconButton;
import cloud.lemonslice.silveroak.helper.GuiHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static cloud.lemonslice.contact.Contact.MODID;

public class WrappingPaperGui extends ContainerScreen<WrappingPaperContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/wrapping_paper.png");
    private int offsetX;
    private int offsetY;
    private IconButton buttonPack;

    public WrappingPaperGui(WrappingPaperContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.ySize = 133;
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - this.xSize) / 2;
        this.offsetY = (this.height - this.ySize) / 2;

        this.buttonPack = new IconButton(offsetX + 124, offsetY + 15, 18, 19, new TranslationTextComponent("tooltip.contact.wrapping_paper.pack"), button -> pack(), this::buttonTooltip);
        this.children.add(this.buttonPack);
    }

    private void buttonTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (button.isHovered())
        {
            GuiHelper.drawTooltip(this, matrixStack, mouseX, mouseY, button.x, button.y, 18, 19, Lists.newArrayList(button.getMessage()));
        }
    }

    private void pack()
    {
        SimpleNetworkHandler.CHANNEL.sendToServer(new ActionMessage(0));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        GuiHelper.drawLayer(matrixStack, offsetX, offsetY, TEXTURE, new TexturePos(0, 0, xSize, ySize));

        GuiHelper.renderButton(matrixStack, partialTicks, x, y, TEXTURE, buttonPack,
                new TexturePos(xSize, 0, 18, 19),
                new TexturePos(xSize, 19, 18, 19));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
    {
    }
}
