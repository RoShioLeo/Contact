package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.Style;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class PostcardReadGui extends Screen
{
    private String page = "";
    private final PostcardStyle style;

    private PostcardEditGui.Page currentPage = PostcardEditGui.Page.EMPTY;

    public PostcardReadGui(ItemStack postcardIn)
    {
        super(NarratorChatListener.EMPTY);
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
            INBT nbt = compoundnbt.get("Text");
            if (nbt != null)
            {
                this.page = nbt.copy().getString();
            }
        }
        else style = PostcardStyle.DEFAULT;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))
        {
            this.closeScreen();
            return true;
        }
        return false;
    }

    @Override
    protected void init()
    {
        this.currentPage = this.createPage();
    }

    private PostcardEditGui.Page createPage()
    {
        if (page.isEmpty())
        {
            return PostcardEditGui.Page.EMPTY;
        }
        else
        {
            IntList intlist = new IntArrayList();
            List<PostcardEditGui.Line> lines = Lists.newArrayList();
            MutableInt mutableint = new MutableInt();
            MutableBoolean mutableboolean = new MutableBoolean();
            CharacterManager charactermanager = this.font.getCharacterManager();
            charactermanager.func_238353_a_(page, style.textWidth, Style.EMPTY, true, (style, lineStartPos, lineEndPos) ->
            {
                int lineCount = mutableint.getAndIncrement();
                String lineTextRaw = page.substring(lineStartPos, lineEndPos);
                mutableboolean.setValue(lineTextRaw.endsWith("\n"));
                String lineText = StringUtils.stripEnd(lineTextRaw, " \n");
                int y = lineCount * 12;
                PostcardEditGui.Point point = this.getScreenPoint(new PostcardEditGui.Point(0, y));
                intlist.add(lineStartPos);
                lines.add(new PostcardEditGui.Line(style, lineText, point.x, point.y));
            });
            int[] linesStartPos = intlist.toIntArray();

            return new PostcardEditGui.Page(page, new PostcardEditGui.Point(0, 0), true, linesStartPos, lines.toArray(new PostcardEditGui.Line[0]), new Rectangle2d[0]);
        }
    }

    private PostcardEditGui.Point getScreenPoint(PostcardEditGui.Point pointIn)
    {
        return new PostcardEditGui.Point(pointIn.x + (this.width - style.cardWidth) / 2 + style.textPosX, pointIn.y + style.textPosY + (this.height - style.cardHeight) / 2);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.setListener(null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(style.getCardTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2, (this.height - style.cardHeight) / 2, style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        RenderSystem.enableBlend();
        RenderSystem.color4f(ColorHelper.getRedF(style.postmarkColor), ColorHelper.getGreenF(style.postmarkColor), ColorHelper.getBlueF(style.postmarkColor), ColorHelper.getAlphaF(style.postmarkColor));

        minecraft.getTextureManager().bindTexture(style.getPostmarkTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2 + style.postmarkPosX, (this.height - style.cardHeight) / 2 + style.postmarkPosY, style.postmarkWidth, style.postmarkHeight, 0, 0, style.postmarkWidth, style.postmarkHeight, style.postmarkWidth, style.postmarkHeight);
        RenderSystem.disableBlend();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        for (PostcardEditGui.Line line : currentPage.lines)
        {
            this.font.drawText(matrixStack, line.lineTextComponent, (float) line.x, (float) line.y, style.textColor);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
