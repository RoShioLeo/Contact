package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.client.widget.EditableTextBox;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

public class PostcardEditGui extends Screen
{
    private final PostcardStyle style;
    private EditableTextBox textBox;
    private Button buttonDone;
    private final ItemStack postcard;
    private final PlayerEntity editingPlayer;
    private final Hand hand;

    public PostcardEditGui(ItemStack postcardIn, PlayerEntity playerIn, Hand handIn)
    {
        super(StringTextComponent.EMPTY);
        this.postcard = postcardIn;
        this.editingPlayer = playerIn;
        this.hand = handIn;
        CompoundNBT compoundnbt = postcardIn.getTag();
        if (compoundnbt != null)
        {
            if (compoundnbt.contains("Info"))
            {
                style = PostcardStyle.fromNBT(compoundnbt);
            }
            else if (compoundnbt.contains("CardID"))
            {
                style = PostcardStyle.fromNBT(compoundnbt);
            }
            else style = PostcardStyle.DEFAULT;
        }
        else style = PostcardStyle.DEFAULT;
        this.textBox = new EditableTextBox(postcard, editingPlayer, hand, (this.width - style.cardWidth) / 2 + style.textPosX, (this.height / 2 - style.cardHeight * 2 / 3) + style.textPosY, style.textWidth, style.textHeight, 12, style.textColor, new StringTextComponent("Postcard"));
    }

    @Override
    protected void init()
    {
        this.buttonDone = this.addButton(new Button(this.width / 2 - 48, this.height / 2 + style.cardHeight / 3 + 20, 98, 20, DialogTexts.GUI_DONE, (button) ->
        {
            this.minecraft.setScreen(null);
            textBox.sendTextToServer();
        }));
        this.children.add(buttonDone);

        this.textBox.init();
        this.textBox.x = (this.width - style.cardWidth) / 2 + style.textPosX;
        this.textBox.y = (this.height / 2 - style.cardHeight * 2 / 3) + style.textPosY;
        this.children.add(textBox);
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

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        minecraft.getTextureManager().bind(style.getCardTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2, this.height / 2 - style.cardHeight * 2 / 3, style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        textBox.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
