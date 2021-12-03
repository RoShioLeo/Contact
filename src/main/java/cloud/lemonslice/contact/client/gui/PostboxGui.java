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
        this.imageHeight = 133;
        this.isRed = screenContainer.getType() == RED_POSTBOX_CONTAINER;
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - this.imageWidth) / 2;
        this.offsetY = (this.height - this.imageHeight) / 2;

        this.buttonSend = new IconButton(offsetX + 97, offsetY + 26, 10, 9, new TranslationTextComponent("tooltip.contact.postbox.send"), button -> send(), this::buttonTooltip);
        this.children.add(this.buttonSend);

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.nameField = new TextFieldWidget(this.font, offsetX + 42, offsetY + 26, 43, 9, new TranslationTextComponent("info.contact.postbox.addressee"));
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setValue(menu.playerName);
        this.nameField.setResponder(this::whileTyping);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(64);
        this.children.add(this.nameField);
        this.setInitialFocus(this.nameField);
    }

    private void buttonTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (button.isHovered())
        {
            if (menu.status == 2)
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
        if (!menu.playerName.equals(name))
        {
            menu.playerName = name;
            if (menu.status > 1)
            {
                menu.status = 1;
            }
        }
    }

    private void send()
    {
        if (menu.status == 2)
        {
            SimpleNetworkHandler.CHANNEL.sendToServer(new EnquireAddresseeMessage(menu.playerName, true));
        }
        else if (menu.status == 5)
        {
            menu.status = 0;
        }
        else if (menu.status != 0)
        {
            SimpleNetworkHandler.CHANNEL.sendToServer(new EnquireAddresseeMessage(menu.playerName, false));
        }
        this.nameField.setFocus(false);
    }

    @Override
    public void tick()
    {
        super.tick();
        this.nameField.tick();
        if (menu.status == 2 && !nameField.getValue().equals(menu.playerName))
        {
            nameField.setValue(menu.playerName);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation texture = isRed ? RED_TEXTURE : GREEN_TEXTURE;

        GuiHelper.drawLayer(matrixStack, offsetX, offsetY, texture, new TexturePos(0, 0, imageWidth, imageHeight));

        GuiHelper.renderButton(matrixStack, partialTicks, x, y, texture, buttonSend,
                new TexturePos(imageWidth, 0, 10, 9),
                new TexturePos(imageWidth, 9, 10, 9));
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.font.draw(matrixStack, new TranslationTextComponent("info.contact.postbox.addressee"), 40, 14, 0xE6E6E6);
        switch (menu.status)
        {
            case 0:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.need_mail");
                int width = this.font.width(text.getString());
                if (width > 38)
                {
                    List<IReorderingProcessor> list = this.font.split(text, 50);
                    for (int i = 0; i < list.size(); i++)
                    {
                        this.font.draw(matrixStack, list.get(i), 118, 22 - list.size() * 6 + i * 12, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.draw(matrixStack, text, 122, 16, 0x1A1A1A);
                }
                return;
            }
            case 2:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.estimated");
                int width = this.font.width(text.getString());
                int min = menu.time / 1200;
                int sec = menu.time % 1200 / 20;
                if (width > 38)
                {
                    this.font.draw(matrixStack, text, 141 - width / 2, 10, 0x1A1A1A);
                    if (menu.time == 0)
                    {
                        this.font.draw(matrixStack, new TranslationTextComponent("info.contact.postbox.instant"), 141 - width / 2, 22, 0x1A1A1A);
                    }
                    else
                    {
                        this.font.draw(matrixStack, new TranslationTextComponent("info.contact.postbox.time", min, sec), 141 - width / 2, 22, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.draw(matrixStack, text, 122, 10, 0x1A1A1A);
                    if (menu.time == 0)
                    {
                        this.font.draw(matrixStack, new TranslationTextComponent("info.contact.postbox.instant"), 122, 22, 0x1A1A1A);
                    }
                    else
                    {
                        this.font.draw(matrixStack, new TranslationTextComponent("info.contact.postbox.time", min, sec), 122, 22, 0x1A1A1A);
                    }
                }
                return;
            }
            case 3:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.not_found");
                int width = this.font.width(text.getString());
                if (width > 38)
                {
                    this.font.draw(matrixStack, text, 141 - width / 2, 16, 0x1A1A1A);
                }
                else
                {
                    this.font.draw(matrixStack, text, 122, 16, 0x1A1A1A);
                }
                return;
            }
            case 4:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.full_mail");
                int width = this.font.width(text.getString());
                if (width > 38)
                {
                    List<IReorderingProcessor> list = this.font.split(text, 50);
                    for (int i = 0; i < list.size(); i++)
                    {
                        this.font.draw(matrixStack, list.get(i), 118, 22 - list.size() * 6 + i * 12, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.draw(matrixStack, text, 122, 16, 0x1A1A1A);
                }
                return;
            }
            case 5:
            {
                TranslationTextComponent text = new TranslationTextComponent("info.contact.postbox.success");
                int width = this.font.width(text.getString());
                if (width > 38)
                {
                    List<IReorderingProcessor> list = this.font.split(text, 50);
                    for (int i = 0; i < list.size(); i++)
                    {
                        this.font.draw(matrixStack, list.get(i), 118, 22 - list.size() * 6 + i * 12, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.draw(matrixStack, text, 122, 16, 0x1A1A1A);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.minecraft.player.closeContainer();
        }

        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }
}
