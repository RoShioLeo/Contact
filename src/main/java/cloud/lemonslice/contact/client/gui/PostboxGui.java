package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.common.container.PostboxContainer;
import cloud.lemonslice.contact.network.EnquireAddresseeMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import cloud.lemonslice.silveroak.client.widget.IconButton;
import cloud.lemonslice.silveroak.helper.GuiHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

import static cloud.lemonslice.contact.Contact.MODID;
import static cloud.lemonslice.contact.common.container.MenuTypeRegistry.RED_POSTBOX_CONTAINER;

public class PostboxGui extends AbstractContainerScreen<PostboxContainer>
{
    private static final ResourceLocation RED_TEXTURE = new ResourceLocation(MODID, "textures/gui/red_postbox.png");
    private static final ResourceLocation GREEN_TEXTURE = new ResourceLocation(MODID, "textures/gui/green_postbox.png");
    private final boolean isRed;
    private int offsetX;
    private int offsetY;
    private IconButton buttonSend;
    private EditBox nameField;

    public PostboxGui(PostboxContainer screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.imageHeight = 133;
        this.isRed = screenContainer.getType() == RED_POSTBOX_CONTAINER.get();
    }

    @Override
    protected void init()
    {
        super.init();
        this.offsetX = (this.width - this.imageWidth) / 2;
        this.offsetY = (this.height - this.imageHeight) / 2;

        this.buttonSend = this.addWidget(new IconButton(offsetX + 97, offsetY + 26, 10, 9, Component.translatable("tooltip.contact.postbox.send"), button -> send(), this::buttonTooltip));

        this.nameField = this.addRenderableWidget(new EditBox(this.font, offsetX + 42, offsetY + 26, 43, 9, Component.translatable("info.contact.postbox.addressee")));
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setValue(menu.playerName);
        this.nameField.setResponder(this::whileTyping);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(64);
        this.setInitialFocus(this.nameField);
    }

    private void buttonTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY)
    {
        if (button.isHoveredOrFocused())
        {
            if (menu.status == 2)
            {
                GuiHelper.drawTooltip(this, poseStack, mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight(), Lists.newArrayList(button.getMessage()));
            }
            else
            {
                GuiHelper.drawTooltip(this, poseStack, mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight(), Lists.newArrayList(Component.translatable("tooltip.contact.postbox.enquire")));
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
    public void containerTick()
    {
        super.containerTick();
        this.nameField.tick();
        if (menu.status == 2 && !nameField.getValue().equals(menu.playerName))
        {
            nameField.setValue(menu.playerName);
        }
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
        ResourceLocation texture = isRed ? RED_TEXTURE : GREEN_TEXTURE;
        RenderSystem.setShaderTexture(0, texture);

        GuiHelper.drawLayer(poseStack, offsetX, offsetY, new TexturePos(0, 0, imageWidth, imageHeight));

        GuiHelper.renderButton(poseStack, partialTicks, x, y, this.getBlitOffset(), texture, buttonSend,
                new TexturePos(imageWidth, 0, 10, 9),
                new TexturePos(imageWidth, 9, 10, 9));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        this.font.draw(poseStack, Component.translatable("info.contact.postbox.addressee"), 40, 14, 0xE6E6E6);
        switch (menu.status)
        {
            case 0:
            {
                MutableComponent text = Component.translatable("info.contact.postbox.need_mail");
                renderTipsSentOrFull(poseStack, text);
                return;
            }
            case 2:
            {
                MutableComponent text = Component.translatable("info.contact.postbox.estimated");
                int width = this.font.width(text.getString());
                int min = menu.time / 1200;
                int sec = menu.time % 1200 / 20;
                if (width > 38)
                {
                    this.font.draw(poseStack, text, 141 - width / 2, 10, 0x1A1A1A);
                    if (menu.time == 0)
                    {
                        this.font.draw(poseStack, Component.translatable("info.contact.postbox.instant"), 141 - width / 2, 22, 0x1A1A1A);
                    }
                    else
                    {
                        this.font.draw(poseStack, Component.translatable("info.contact.postbox.time", min, sec), 141 - width / 2, 22, 0x1A1A1A);
                    }
                }
                else
                {
                    this.font.draw(poseStack, text, 122, 10, 0x1A1A1A);
                    if (menu.time == 0)
                    {
                        this.font.draw(poseStack, Component.translatable("info.contact.postbox.instant"), 122, 22, 0x1A1A1A);
                    }
                    else
                    {
                        this.font.draw(poseStack, Component.translatable("info.contact.postbox.time", min, sec), 122, 22, 0x1A1A1A);
                    }
                }
                return;
            }
            case 3:
            {
                MutableComponent text = Component.translatable("info.contact.postbox.not_found");
                int width = this.font.width(text.getString());
                if (width > 38)
                {
                    this.font.draw(poseStack, text, 141 - width / 2, 16, 0x1A1A1A);
                }
                else
                {
                    this.font.draw(poseStack, text, 122, 16, 0x1A1A1A);
                }
                return;
            }
            case 4:
            {
                MutableComponent text = Component.translatable("info.contact.postbox.full_mail");
                renderTipsSentOrFull(poseStack, text);
                return;
            }
            case 5:
            {
                MutableComponent text = Component.translatable("info.contact.postbox.success");
                renderTipsSentOrFull(poseStack, text);
            }
        }
    }

    private void renderTipsSentOrFull(PoseStack poseStack, MutableComponent text)
    {
        int width = this.font.width(text.getString());
        if (width > 38)
        {
            List<FormattedCharSequence> list = this.font.split(text, 50);
            for (int i = 0; i < list.size(); i++)
            {
                this.font.draw(poseStack, list.get(i), 118, 22 - list.size() * 6 + i * 12, 0x1A1A1A);
            }
        }
        else
        {
            this.font.draw(poseStack, text, 122, 16, 0x1A1A1A);
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
