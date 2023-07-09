package cloud.lemonslice.contact.client.gui.screen;

import cloud.lemonslice.contact.common.screenhandler.PostboxScreenHandler;
import cloud.lemonslice.contact.network.EnquireAddresseeMessage;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

import static cloud.lemonslice.contact.Contact.MODID;
import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.RED_POSTBOX_CONTAINER;

public class PostboxScreen extends HandledScreen<PostboxScreenHandler>
{
    private static final Identifier RED_TEXTURE = new Identifier(MODID, "textures/gui/red_postbox.png");
    private static final Identifier GREEN_TEXTURE = new Identifier(MODID, "textures/gui/green_postbox.png");
    private final boolean isRed;
    private int offsetX;
    private int offsetY;
    private IconButton buttonSend;
    private TextFieldWidget nameField;
    private int selected = 0;

    public PostboxScreen(PostboxScreenHandler screenContainer, PlayerInventory inv, Text titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.isRed = screenContainer.getType() == RED_POSTBOX_CONTAINER;
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - 176) / 2;
        this.offsetY = (this.height - 166) / 2 + 16;

        this.buttonSend = this.addDrawableChild(new IconButton(offsetX + 97, offsetY + 26, 10, 9, Text.translatable("tooltip.contact.postbox.send"), button -> send(), this::buttonTooltip));

        this.nameField = this.addDrawableChild(new TextFieldWidget(this.textRenderer, offsetX + 42, offsetY + 26, 44, 9, Text.translatable("info.contact.postbox.addressee")));
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setText(handler.playerName);
        this.nameField.setChangedListener(this::whileTyping);
        this.nameField.setDrawsBackground(false);
        this.nameField.setMaxLength(64);
        this.setInitialFocus(this.nameField);
    }

    private void buttonTooltip(ButtonWidget button, DrawContext drawContext, int mouseX, int mouseY)
    {
        if (button.isHovered())
        {
            GuiHelper.drawTooltip(drawContext, mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight(), Lists.newArrayList(button.getMessage()));
        }
    }

    private void whileTyping(String name)
    {
        if (!handler.playerName.equals(name))
        {
            handler.playerName = name;
            if (handler.status == 2)
            {
                handler.status = 1;
            }
            if (handler.status == 1)
            {
                ClientPlayNetworking.send(EnquireAddresseeMessage.getID(), EnquireAddresseeMessage.create(handler.playerName, false).toBytes());
            }
        }
    }

    private void send()
    {
        if (handler.status == 1)
        {
            if (isAddresseeValid() && handler.ticks.get(0) >= 0)
            {
                ClientPlayNetworking.send(EnquireAddresseeMessage.getID(), EnquireAddresseeMessage.create(handler.playerName, true).toBytes());
            }
        }
        else if (handler.status == 2)
        {
            handler.status = 0;
        }
        this.nameField.setFocused(false);
    }

    private boolean isAddresseeValid()
    {
        return !handler.names.isEmpty() && Objects.equals(nameField.getText(), handler.names.get(0));
    }

    @Override
    protected void handledScreenTick()
    {
        super.handledScreenTick();
        this.nameField.tick();
        if (handler.status == 1 && !nameField.getText().equals(handler.playerName))
        {
            nameField.setText(handler.playerName);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (handler.status == 1 && nameField.isFocused())
        {
            int size = handler.names.size();

            int maxWidth = 55;
            for (int i = 0; i < size; i++)
            {
                maxWidth = Math.max(this.textRenderer.getWidth(handler.names.get(i)) + 8, maxWidth);
            }

            if (size != 0)
            {
                if (offsetX + 42 <= mouseX && mouseX < offsetX + 42 + maxWidth && offsetY + 40 + selected * 11 <= mouseY && mouseY < offsetY + 51 + selected * 11)
                {
                    this.nameField.setText(handler.names.get(selected));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(drawContext);

        super.render(drawContext, mouseX, mouseY, partialTicks);

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0f, 0.0f, 400.0f);
        boolean flag = false;
        if (handler.status == 1 && nameField.isFocused())
        {
            int size = handler.names.size();

            int maxWidth = 55;
            for (int i = 0; i < size; i++)
            {
                maxWidth = Math.max(this.textRenderer.getWidth(handler.names.get(i)) + 8, maxWidth);
            }

            int z = 5000;
            if (size != 0)
            {
                Identifier texture = isRed ? RED_TEXTURE : GREEN_TEXTURE;
//                RenderSystem.setShaderTexture(0, texture);
                int renderWidth = maxWidth;
                if (renderWidth == 55)
                {
                    GuiHelper.drawLayer(drawContext, offsetX + 38, offsetY + 36, texture, new TexturePos(176, 18, 55, 3 + 11 * size));
                    GuiHelper.drawLayer(drawContext, offsetX + 38, offsetY + 39 + 11 * size, texture, new TexturePos(176, 65, 55, 2));
                }
                else
                {
                    GuiHelper.drawLayer(drawContext, offsetX + 38, offsetY + 36, texture, new TexturePos(176, 18, 15, 3 + 11 * size));
                    GuiHelper.drawLayer(drawContext, offsetX + 38, offsetY + 39 + 11 * size, texture, new TexturePos(176, 65, 15, 2));

                    GuiHelper.drawLayer(drawContext, offsetX + 23 + renderWidth, offsetY + 36, texture, new TexturePos(216, 18, 15, 3 + 11 * size));
                    GuiHelper.drawLayer(drawContext, offsetX + 23 + renderWidth, offsetY + 39 + 11 * size, texture, new TexturePos(216, 65, 15, 2));

                    renderWidth -= 30;
                    int pos = 0;

                    while (renderWidth > 15)
                    {
                        GuiHelper.drawLayer(drawContext, offsetX + 53 + pos, offsetY + 36, texture, new TexturePos(191, 18, 15, 3 + 11 * size));
                        GuiHelper.drawLayer(drawContext, offsetX + 53 + pos, offsetY + 39 + 11 * size, texture, new TexturePos(191, 65, 15, 2));

                        renderWidth -= 15;
                        pos += 15;
                    }
                    GuiHelper.drawLayer(drawContext, offsetX + 53 + pos, offsetY + 36, texture, new TexturePos(191, 18, renderWidth, 3 + 11 * size));
                    GuiHelper.drawLayer(drawContext, offsetX + 53 + pos, offsetY + 39 + 11 * size, texture, new TexturePos(191, 65, renderWidth, 2));
                }
            }

            for (int i = 0; i < size; i++)
            {
                if (offsetX + 42 <= mouseX && mouseX < offsetX + 42 + maxWidth && offsetY + 40 + i * 11 <= mouseY && mouseY < offsetY + 51 + i * 11)
                {
                    selected = i;
                    flag = true;
                }
                drawContext.drawText(this.textRenderer, handler.names.get(i), offsetX + 42, offsetY + 40 + i * 11, selected == i ? Formatting.YELLOW.getColorValue() : Formatting.WHITE.getColorValue(), false);
            }
        }
        drawContext.getMatrices().pop();

        if (!flag)
        {
            this.drawMouseoverTooltip(drawContext, mouseX, mouseY);
        }
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTicks, int x, int y)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Identifier texture = isRed ? RED_TEXTURE : GREEN_TEXTURE;
        RenderSystem.setShaderTexture(0, texture);

        GuiHelper.drawLayer(drawContext.getMatrices(), offsetX, offsetY, new TexturePos(0, 0, 176, 133));

        GuiHelper.renderButton(drawContext, partialTicks, x, y, 0, texture, buttonSend,
                new TexturePos(176, 0, 10, 9),
                new TexturePos(176, 9, 10, 9));
    }

    @Override
    protected void drawForeground(DrawContext drawContext, int mouseX, int mouseY)
    {
        drawContext.drawText(this.textRenderer, Text.translatable("info.contact.postbox.addressee"), 40, 30, 0xE6E6E6, false);
        switch (handler.status)
        {
            case 0 ->
            {
                MutableText text = Text.translatable("info.contact.postbox.need_mail");
                renderTips(drawContext, text);
            }
            case 1 ->
            {
                if (isAddresseeValid())
                {
                    int tick = handler.ticks.get(0);
                    if (tick < 0)
                    {
                        MutableText text = Text.translatable("info.contact.postbox.no_mailbox");
                        renderTips(drawContext, text);
                    }
                    else
                    {
                        MutableText text = Text.translatable("info.contact.postbox.estimated");
                        int width = this.textRenderer.getWidth(text.getString());
                        int min = tick / 1200;
                        int sec = tick % 1200 / 20;
                        if (width > 38)
                        {
                            drawContext.drawText(this.textRenderer, text, 141 - width / 2, 26, 0x1A1A1A, false);
                            if (tick < 20)
                            {
                                drawContext.drawText(this.textRenderer, Text.translatable("info.contact.postbox.instant"), 141 - width / 2, 38, 0x1A1A1A, false);
                            }
                            else
                            {
                                drawContext.drawText(this.textRenderer, Text.translatable("info.contact.postbox.time", min, sec), 141 - width / 2, 38, 0x1A1A1A, false);
                            }
                        }
                        else
                        {
                            drawContext.drawText(this.textRenderer, text, 122, 26, 0x1A1A1A, false);
                            if (tick < 20)
                            {
                                drawContext.drawText(this.textRenderer, Text.translatable("info.contact.postbox.instant"), 122, 38, 0x1A1A1A, false);
                            }
                            else
                            {
                                drawContext.drawText(this.textRenderer, Text.translatable("info.contact.postbox.time", min, sec), 122, 38, 0x1A1A1A, false);
                            }
                        }
                    }
                }
                else
                {
                    MutableText text = Text.translatable("info.contact.postbox.need_addressee");
                    renderTips(drawContext, text);
//                    MutableText text = Text.translatable("info.contact.postbox.need_addressee");
//                    int width = this.textRenderer.getWidth(text.getString());
//                    if (width > 38)
//                    {
//                        this.textRenderer.draw(matrixStack, text, 141 - width / 2, 32, 0x1A1A1A);
//                    }
//                    else
//                    {
//                        this.textRenderer.draw(matrixStack, text, 122, 32, 0x1A1A1A);
//                    }
                }
            }
            case 2 ->
            {
                MutableText text = Text.translatable("info.contact.postbox.success");
                renderTips(drawContext, text);
            }
            case 3 ->
            {
                MutableText text = Text.translatable("info.contact.postbox.cannot_send");
                renderTips(drawContext, text);
            }
        }
    }

    private void renderTips(DrawContext drawContext, MutableText text)
    {
        int width = this.textRenderer.getWidth(text.getString());
        if (width > 38)
        {
            List<OrderedText> list = this.textRenderer.wrapLines(text, 50);
            for (int i = 0; i < list.size(); i++)
            {
                drawContext.drawText(this.textRenderer, list.get(i), 118, 38 - list.size() * 6 + i * 12, 0x1A1A1A, false);
            }
        }
        else
        {
            drawContext.drawText(this.textRenderer, text, 122, 32, 0x1A1A1A, false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.client.player.closeHandledScreen();
        }

        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isFocused() || super.keyPressed(keyCode, scanCode, modifiers);
    }
}
