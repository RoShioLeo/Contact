package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.common.container.PostboxContainer;
import cloud.lemonslice.contact.network.EnquireAddresseeMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import cloud.lemonslice.silveroak.client.widget.IconButton;
import cloud.lemonslice.silveroak.helper.GuiHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static cloud.lemonslice.contact.Contact.MODID;
import static cloud.lemonslice.contact.common.container.ContainerTypeRegistry.RED_POSTBOX_CONTAINER;

public class PostboxGui extends ContainerScreen<PostboxContainer>
{
    private static final ResourceLocation RED_TEXTURE = new ResourceLocation(MODID, "textures/gui/red_postbox.png");
    private static final ResourceLocation GREEN_TEXTURE = new ResourceLocation(MODID, "textures/gui/green_postbox.png");
    private final boolean isRed;
    private int offsetX;
    private int offsetY;
    private IconButton buttonSend;
    private TextFieldWidget nameField;

    public PostboxGui(PostboxContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.ySize = 133;
        this.isRed = screenContainer.getType() == RED_POSTBOX_CONTAINER;
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - this.xSize) / 2;
        this.offsetY = (this.height - this.ySize) / 2;

        this.buttonSend = new IconButton(offsetX + 97, offsetY + 26, 10, 9, new TranslationTextComponent("tooltip.contact.postbox.send"), button -> send(), this::buttonTooltip);
        this.children.add(this.buttonSend);

        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.nameField = new TextFieldWidget(this.font, offsetX + 42, offsetY + 26, 43, 9, new TranslationTextComponent("info.contact.postbox.addressee"));
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setText(container.playerName);
        this.nameField.setResponder(this::whileTyping);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(64);
        this.children.add(this.nameField);
        this.setFocusedDefault(this.nameField);
    }

    private void buttonTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (button.isHovered())
        {
            if (container.status == 2)
            {
                GuiHelper.drawTooltip(this, matrixStack, mouseX, mouseY, button.x, button.y, button.getWidth(), button.getHeight(), Lists.newArrayList(button.getMessage()));
            }
            else
            {
                GuiHelper.drawTooltip(this, matrixStack, mouseX, mouseY, button.x, button.y, button.getWidth(), button.getHeight(), Lists.newArrayList(new TranslationTextComponent("tooltip.contact.postbox.enquire")));
            }
        }
    }

    private void whileTyping(String name)
    {
        if (!container.playerName.equals(name))
        {
            container.playerName = name;
            if (container.status > 1)
            {
                container.status = 1;
            }
        }
    }

    private void send()
    {
        if (container.status == 2)
        {
            SimpleNetworkHandler.CHANNEL.sendToServer(new EnquireAddresseeMessage(container.playerName, true));
        }
        else if (container.status == 5)
        {
            container.status = 0;
        }
        else if (container.status != 0)
        {
            SimpleNetworkHandler.CHANNEL.sendToServer(new EnquireAddresseeMessage(container.playerName, false));
        }
        this.nameField.setFocused2(false);
    }

    @Override
    public void tick()
    {
        super.tick();
        this.nameField.tick();
        if (container.status == 2 && !nameField.getText().equals(container.playerName))
        {
            nameField.setText(container.playerName);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation texture = isRed ? RED_TEXTURE : GREEN_TEXTURE;

        GuiHelper.drawLayer(matrixStack, offsetX, offsetY, texture, new TexturePos(0, 0, xSize, ySize));

        GuiHelper.renderButton(matrixStack, partialTicks, x, y, texture, buttonSend,
                new TexturePos(xSize, 0, 10, 9),
                new TexturePos(xSize, 9, 10, 9));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.font.drawText(matrixStack, new TranslationTextComponent("info.contact.postbox.addressee"), 40, 14, 0xE6E6E6);
        switch (container.status)
        {
            case 0:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.need_mail");
                int width = this.font.getStringWidth(text.getString());
                if (width > 38)
                {
                    List<IReorderingProcessor> list = this.font.trimStringToWidth(text, 50);
                    for (int i = 0; i < list.size(); i++)
                    {
                        this.font.func_238422_b_(matrixStack, list.get(i), 118, 22 - list.size() * 6 + i * 12, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.drawText(matrixStack, text, 122, 16, 0x1A1A1A);
                }
                return;
            }
            case 2:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.estimated");
                int width = this.font.getStringWidth(text.getString());
                int min = container.time / 1200;
                int sec = container.time % 1200 / 20;
                if (width > 38)
                {
                    this.font.drawText(matrixStack, text, 141 - width / 2, 10, 0x1A1A1A);
                    if (container.time == 0)
                    {
                        this.font.drawText(matrixStack, new TranslationTextComponent("info.contact.postbox.instant"), 141 - width / 2, 22, 0x1A1A1A);
                    }
                    else
                    {
                        this.font.drawText(matrixStack, new TranslationTextComponent("info.contact.postbox.time", min, sec), 141 - width / 2, 22, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.drawText(matrixStack, text, 122, 10, 0x1A1A1A);
                    if (container.time == 0)
                    {
                        this.font.drawText(matrixStack, new TranslationTextComponent("info.contact.postbox.instant"), 122, 22, 0x1A1A1A);
                    }
                    else
                    {
                        this.font.drawText(matrixStack, new TranslationTextComponent("info.contact.postbox.time", min, sec), 122, 22, 0x1A1A1A);
                    }
                }
                return;
            }
            case 3:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.not_found");
                int width = this.font.getStringWidth(text.getString());
                if (width > 38)
                {
                    this.font.drawText(matrixStack, text, 141 - width / 2, 16, 0x1A1A1A);
                }
                else
                {
                    this.font.drawText(matrixStack, text, 122, 16, 0x1A1A1A);
                }
                return;
            }
            case 4:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.full_mail");
                int width = this.font.getStringWidth(text.getString());
                if (width > 38)
                {
                    List<IReorderingProcessor> list = this.font.trimStringToWidth(text, 50);
                    for (int i = 0; i < list.size(); i++)
                    {
                        this.font.func_238422_b_(matrixStack, list.get(i), 118, 22 - list.size() * 6 + i * 12, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.drawText(matrixStack, text, 122, 16, 0x1A1A1A);
                }
                return;
            }
            case 5:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.success");
                int width = this.font.getStringWidth(text.getString());
                if (width > 38)
                {
                    List<IReorderingProcessor> list = this.font.trimStringToWidth(text, 50);
                    for (int i = 0; i < list.size(); i++)
                    {
                        this.font.func_238422_b_(matrixStack, list.get(i), 118, 22 - list.size() * 6 + i * 12, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.drawText(matrixStack, text, 122, 16, 0x1A1A1A);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.minecraft.player.closeScreen();
        }

        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.canWrite() || super.keyPressed(keyCode, scanCode, modifiers);
    }
}
