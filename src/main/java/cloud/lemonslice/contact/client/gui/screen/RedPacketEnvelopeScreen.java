package cloud.lemonslice.contact.client.gui.screen;

import cloud.lemonslice.contact.common.screenhandler.RedPacketEnvelopeScreenHandler;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import cloud.lemonslice.silveroak.client.widget.IconButton;
import cloud.lemonslice.silveroak.helper.GuiHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.Contact.MODID;

public class RedPacketEnvelopeScreen extends HandledScreen<RedPacketEnvelopeScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier(MODID, "textures/gui/red_packet.png");
    private int offsetX;
    private int offsetY;
    private IconButton buttonPack;
    private TextFieldWidget blessings;

    public RedPacketEnvelopeScreen(RedPacketEnvelopeScreenHandler screenContainer, PlayerInventory inv, Text titleIn)
    {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - 176) / 2;
        this.offsetY = (this.height - 166) / 2 + 16;

        this.buttonPack = addDrawableChild(new IconButton(offsetX + 130, offsetY + 18, 8, 19, Text.translatable("tooltip.contact.envelope.seal"), button -> seal(), this::buttonTooltip));

        this.blessings = this.addDrawableChild(new TextFieldWidget(this.textRenderer, offsetX + 66, offsetY + 26, 52, 9, Text.translatable("info.contact.envelope.blessings")));
        this.blessings.setEditableColor(-1);
        this.blessings.setUneditableColor(-1);
        this.blessings.setText(handler.blessings);
        this.blessings.setChangedListener(this::whileTyping);
        this.blessings.setDrawsBackground(false);
        this.blessings.setMaxLength(64);
        this.setInitialFocus(this.blessings);
    }

    private void buttonTooltip(ButtonWidget button, DrawContext drawContext, int mouseX, int mouseY)
    {
        if (button.isHovered())
        {
            GuiHelper.drawTooltip(drawContext, mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight(), Lists.newArrayList(button.getMessage()));
        }
    }

    private void seal()
    {
        ClientPlayNetworking.send(ActionMessage.getID(), ActionMessage.create(0, handler.blessings).toBytes());
    }

    private void whileTyping(String blessings)
    {
        if (!handler.blessings.equals(blessings))
        {
            handler.blessings = blessings;
        }
    }

    @Override
    protected void handledScreenTick()
    {
        super.handledScreenTick();
        this.blessings.tick();
    }

    @Override
    protected void drawForeground(DrawContext drawContext, int mouseX, int mouseY)
    {
        drawContext.drawText(this.textRenderer, Text.translatable("info.contact.red_packet.blessings"), 64, 30, 0xE6E6E6, false);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, TEXTURE);
        GuiHelper.drawLayer(drawContext.getMatrices(), offsetX, offsetY, new TexturePos(0, 0, 176, 133));

        GuiHelper.renderButton(drawContext, delta, x, y, 0, TEXTURE, buttonPack,
                new TexturePos(176, 0, 18, 19),
                new TexturePos(176, 19, 18, 19));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.client.player.closeHandledScreen();
        }

        return this.blessings.keyPressed(keyCode, scanCode, modifiers) || this.blessings.isFocused() || super.keyPressed(keyCode, scanCode, modifiers);
    }
}
