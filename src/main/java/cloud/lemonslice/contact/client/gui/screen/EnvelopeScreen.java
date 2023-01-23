package cloud.lemonslice.contact.client.gui.screen;

import cloud.lemonslice.contact.common.screenhandler.EnvelopeScreenHandler;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import cloud.lemonslice.silveroak.client.widget.IconButton;
import cloud.lemonslice.silveroak.helper.GuiHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.Contact.MODID;

public class EnvelopeScreen extends HandledScreen<EnvelopeScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(MODID, "textures/gui/envelope.png");
    private int offsetX;
    private int offsetY;
    private IconButton buttonPack;

    public EnvelopeScreen(EnvelopeScreenHandler screenContainer, PlayerInventory inv, Text titleIn)
    {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - 176) / 2;
        this.offsetY = (this.height - 166) / 2 + 16;

        this.buttonPack = addDrawableChild(new IconButton(offsetX + 100, offsetY + 16, 18, 19, Text.translatable("tooltip.contact.envelope.seal"), button -> seal(), this::buttonTooltip));
    }

    private void buttonTooltip(ButtonWidget button, MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (button.isHovered())
        {
            GuiHelper.drawTooltip(this, matrixStack, mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight(), Lists.newArrayList(button.getMessage()));
        }
    }

    private void seal()
    {
        ClientPlayNetworking.send(ActionMessage.getID(), ActionMessage.create(0).toBytes());
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, TEXTURE);
        GuiHelper.drawLayer(matrixStack, offsetX, offsetY, new TexturePos(0, 0, 176, 133));

        GuiHelper.renderButton(matrixStack, delta, x, y, this.getZOffset(), TEXTURE, buttonPack,
                new TexturePos(176, 0, 18, 19),
                new TexturePos(176, 19, 18, 19));
    }
}
