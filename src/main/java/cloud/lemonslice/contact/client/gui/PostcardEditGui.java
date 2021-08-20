package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.contact.network.PostcardEditMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.contact.resourse.PostcardStyle;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class PostcardEditGui extends Screen
{
    private final ItemStack postcard;
    private final PlayerEntity editingPlayer;
    private final Hand hand;

    private String page = "";
    private boolean isModified = false;
    private int updateCount = 0;
    private long lastClickTime = 0;
    private final PostcardStyle style;

    private final TextInputUtil textInputUtil;
    private Button buttonDone;
    @Nullable
    private Page currentPage = Page.EMPTY;

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
            INBT nbt = compoundnbt.get("Text");
            if (nbt != null)
            {
                this.page = nbt.copy().getString();
            }
        }
        else style = PostcardStyle.DEFAULT;
        this.textInputUtil = new TextInputUtil(() -> page, this::setText, this::getClipboardText, this::setClipboardText, (text) -> text.length() < 1024 && this.font.getWordWrappedHeight(text, style.textWidth) <= style.textHeight * 0.75);
    }

    @Override
    protected void init()
    {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.buttonDone = this.addButton(new Button(this.width / 2 - 48, this.height / 2 + style.cardHeight / 3 + 20, 98, 20, DialogTexts.GUI_DONE, (button) ->
        {
            this.minecraft.displayGuiScreen(null);
            this.sendBookToServer();
        }));
        this.children.add(buttonDone);
        this.shouldRefresh();
    }

    @Override
    public void tick()
    {
        super.tick();
        ++this.updateCount;
    }

    @Override
    public void onClose()
    {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    private void sendBookToServer()
    {
        if (this.isModified)
        {
            this.postcard.setTagInfo("Text", StringNBT.valueOf(this.page));
            int i = this.hand == Hand.MAIN_HAND ? this.editingPlayer.inventory.currentItem : 40;
            SimpleNetworkHandler.CHANNEL.sendToServer(new PostcardEditMessage(postcard, i));
        }
    }

    private Page createPage()
    {
        if (page.isEmpty())
        {
            return Page.EMPTY;
        }
        else
        {
            int selectionEnd = this.textInputUtil.getSelectionEnd();
            int selectionStart = this.textInputUtil.getSelectionStart();
            IntList intlist = new IntArrayList();
            List<Line> lines = Lists.newArrayList();
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
                Point point = this.getScreenPoint(new Point(0, y));
                intlist.add(lineStartPos);
                lines.add(new Line(style, lineText, point.x, point.y));
            });
            int[] linesStartPos = intlist.toIntArray();
            boolean flag = selectionEnd == page.length();
            Point point;
            if (flag && mutableboolean.isTrue())
            {
                point = new Point(0, lines.size() * 12);
            }
            else
            {
                int line = getCursorLine(linesStartPos, selectionEnd);
                point = new Point(this.font.getStringWidth(page.substring(linesStartPos[line], selectionEnd)), line * 12);
            }

            List<Rectangle2d> rectangleList = Lists.newArrayList();
            if (selectionEnd != selectionStart)
            {
                int selectionMin = Math.min(selectionEnd, selectionStart);
                int selectionMax = Math.max(selectionEnd, selectionStart);
                int selectionStartLine = getCursorLine(linesStartPos, selectionMin);
                int selectionEndLine = getCursorLine(linesStartPos, selectionMax);
                if (selectionStartLine == selectionEndLine)
                {
                    int linePosY = selectionStartLine * 12;
                    int linePosX = linesStartPos[selectionStartLine];
                    rectangleList.add(this.getRectangle(page, charactermanager, selectionMin, selectionMax, linePosY, linePosX));
                }
                else
                {
                    int i3 = selectionStartLine + 1 > linesStartPos.length ? page.length() : linesStartPos[selectionStartLine + 1];
                    rectangleList.add(this.getRectangle(page, charactermanager, selectionMin, i3, selectionStartLine * 12, linesStartPos[selectionStartLine]));

                    for (int j3 = selectionStartLine + 1; j3 < selectionEndLine; ++j3)
                    {
                        int j2 = j3 * 12;
                        String s1 = page.substring(linesStartPos[j3], linesStartPos[j3 + 1]);
                        int k2 = (int) charactermanager.func_238350_a_(s1);
                        rectangleList.add(this.getRectangle(new Point(0, j2), new Point(k2, j2 + 9)));
                    }

                    rectangleList.add(this.getRectangle(page, charactermanager, linesStartPos[selectionEndLine], selectionMax, selectionEndLine * 12, linesStartPos[selectionEndLine]));
                }
            }

            return new Page(page, point, flag, linesStartPos, lines.toArray(new Line[0]), rectangleList.toArray(new Rectangle2d[0]));
        }
    }

    private Rectangle2d getRectangle(String text, CharacterManager characterManager, int from, int to, int lineStart, int lineEnd)
    {
        String s = text.substring(lineEnd, from);
        String s1 = text.substring(lineEnd, to);
        Point pointFrom = new Point((int) characterManager.func_238350_a_(s), lineStart);
        Point pointTo = new Point((int) characterManager.func_238350_a_(s1), lineStart + 12);
        return this.getRectangle(pointFrom, pointTo);
    }

    private Rectangle2d getRectangle(Point pointFromIn, Point pointToIn)
    {
        Point pointFrom = this.getScreenPoint(pointFromIn);
        Point pointTo = this.getScreenPoint(pointToIn);
        int i = Math.min(pointFrom.x, pointTo.x);
        int j = Math.max(pointFrom.x, pointTo.x);
        int k = Math.min(pointFrom.y, pointTo.y);
        int l = Math.max(pointFrom.y, pointTo.y);
        return new Rectangle2d(i, k, j - i, l - k);
    }

    private Page getPage()
    {
        if (this.currentPage == null)
        {
            this.currentPage = this.createPage();
        }

        return this.currentPage;
    }

    private void setText(String text)
    {
        this.page = text;
        this.isModified = true;
    }

    private String getClipboardText()
    {
        return this.minecraft != null ? TextInputUtil.getClipboardText(this.minecraft) : "";
    }

    private void setClipboardText(String text)
    {
        if (this.minecraft != null)
        {
            TextInputUtil.setClipboardText(this.minecraft, text);
        }
    }

    private void shouldRefresh()
    {
        this.currentPage = null;
    }

    private void getDownLine()
    {
        this.toMoveCursorLine(1);
    }

    private void getUpLine()
    {
        this.toMoveCursorLine(-1);
    }

    private void toMoveCursorLine(int lineAdded)
    {
        int i = this.textInputUtil.getSelectionEnd();
        int j = this.getPage().getLineToMove(i, lineAdded);
        this.textInputUtil.moveCursorTo(j, Screen.hasShiftDown());
    }

    private static int getCursorLine(int[] linesLength, int cursorPos)
    {
        int i = Arrays.binarySearch(linesLength, cursorPos);
        return i < 0 ? -(i + 2) : i;
    }

    private void moveToLineHead()
    {
        int i = this.textInputUtil.getSelectionEnd();
        int j = this.getPage().getLineStartPos(i);
        this.textInputUtil.moveCursorTo(j, Screen.hasShiftDown());
    }

    private void moveToLineEnd()
    {
        int i = this.textInputUtil.getSelectionEnd();
        int j = getPage().getLineEndPos(i);
        this.textInputUtil.moveCursorTo(j, Screen.hasShiftDown());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else
        {
            boolean flag = this.keyPressedInBook(keyCode, scanCode, modifiers);
            if (flag)
            {
                this.shouldRefresh();
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    private boolean keyPressedInBook(int keyCode, int scanCode, int modifiers)
    {
        if (Screen.isSelectAll(keyCode))
        {
            this.textInputUtil.selectAll();
            return true;
        }
        else if (Screen.isCopy(keyCode))
        {
            this.textInputUtil.copySelectedText();
            return true;
        }
        else if (Screen.isPaste(keyCode))
        {
            this.textInputUtil.insertClipboardText();
            return true;
        }
        else if (Screen.isCut(keyCode))
        {
            this.textInputUtil.cutText();
            return true;
        }
        else
        {
            switch (keyCode)
            {
                case 257:
                case 335:
                    this.textInputUtil.putText("\n");
                    return true;
                case 259:
                    this.textInputUtil.deleteCharAtSelection(-1);
                    return true;
                case 261:
                    this.textInputUtil.deleteCharAtSelection(1);
                    return true;
                case 262:
                    this.textInputUtil.moveCursorByChar(1, Screen.hasShiftDown());
                    return true;
                case 263:
                    this.textInputUtil.moveCursorByChar(-1, Screen.hasShiftDown());
                    return true;
                case 264:
                    this.getDownLine();
                    return true;
                case 265:
                    this.getUpLine();
                    return true;
                case 268:
                    this.moveToLineHead();
                    return true;
                case 269:
                    this.moveToLineEnd();
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if (super.charTyped(codePoint, modifiers))
        {
            return true;
        }
        else if (SharedConstants.isAllowedCharacter(codePoint))
        {
            this.textInputUtil.putText(Character.toString(codePoint));
            this.shouldRefresh();
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.setListener(null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        minecraft.getTextureManager().bindTexture(style.getCardTexture());
        blit(matrixStack, (this.width - style.cardWidth) / 2, this.height / 2 - style.cardHeight * 2 / 3, style.cardWidth, style.cardHeight, 0, 0, style.cardWidth, style.cardHeight, style.cardWidth, style.cardHeight);

        Page page = this.getPage();

        for (Line line : page.lines)
        {
            this.font.drawText(matrixStack, line.lineTextComponent, (float) line.x, (float) line.y, style.textColor);
        }

        this.renderSelection(page.selection);
        this.renderCursor(matrixStack, page.point, page.isInsert);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderSelection(Rectangle2d[] selection)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);

        for (Rectangle2d rectangle2d : selection)
        {
            int i = rectangle2d.getX();
            int j = rectangle2d.getY();
            int k = i + rectangle2d.getWidth();
            int l = j + rectangle2d.getHeight();
            bufferbuilder.pos(i, l, 0.0D).endVertex();
            bufferbuilder.pos(k, l, 0.0D).endVertex();
            bufferbuilder.pos(k, j, 0.0D).endVertex();
            bufferbuilder.pos(i, j, 0.0D).endVertex();
        }

        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private void renderCursor(MatrixStack matrixStack, Point point, boolean isInsert)
    {
        if (this.updateCount / 6 % 2 == 0 && style.textHeight != 0)
        {
            point = this.getScreenPoint(point);
            if (!isInsert)
            {
                AbstractGui.fill(matrixStack, point.x, point.y - 1, point.x + 1, point.y + 9, style.textColor);
            }
            else
            {
                this.font.drawString(matrixStack, "_", (float) point.x, (float) point.y, style.textColor);
            }
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (!super.mouseClicked(mouseX, mouseY, button))
        {
            if (button == 0)
            {
                long i = Util.milliTime();
                int j = getPage().getMousePointPosInText(this.font, this.getTexturePoint(new Point((int) mouseX, (int) mouseY)));
                if (j >= 0)
                {
                    if (i - this.lastClickTime < 250L)
                    {
                        if (!this.textInputUtil.hasSelection())
                        {
                            this.setSelection(j);
                        }
                        else
                        {
                            this.textInputUtil.selectAll();
                        }
                    }
                    else
                    {
                        this.textInputUtil.moveCursorTo(j, Screen.hasShiftDown());
                    }

                    this.shouldRefresh();
                }

                this.lastClickTime = i;
            }

        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (!super.mouseDragged(mouseX, mouseY, button, dragX, dragY))
        {
            if (button == 0)
            {
                int i = getPage().getMousePointPosInText(this.font, this.getTexturePoint(new Point((int) mouseX, (int) mouseY)));
                this.textInputUtil.moveCursorTo(i, true);
                this.shouldRefresh();
            }

        }
        return true;
    }

    private void setSelection(int cursorPos)
    {
        this.textInputUtil.setSelection(CharacterManager.func_238351_a_(page, -1, cursorPos, false), CharacterManager.func_238351_a_(page, 1, cursorPos, false));
    }

    private Point getTexturePoint(Point pointIn)
    {
        return new Point(pointIn.x - (this.width - style.cardWidth) / 2 - style.textPosX, pointIn.y - style.textPosY - (this.height / 2 - style.cardHeight * 2 / 3));
    }

    private Point getScreenPoint(Point pointIn)
    {
        return new Point(pointIn.x + (this.width - style.cardWidth) / 2 + style.textPosX, pointIn.y + style.textPosY + (this.height / 2 - style.cardHeight * 2 / 3));
    }

    static class Page
    {
        protected static final Page EMPTY = new Page("", new Point(0, 0), true, new int[]{0}, new Line[]{new Line(Style.EMPTY, "", 0, 0)}, new Rectangle2d[0]);
        private final String text;
        private final Point point;
        private final boolean isInsert;
        private final int[] linesStartPos;
        protected final Line[] lines;
        private final Rectangle2d[] selection;

        public Page(String text, Point point, boolean isInsert, int[] linesStartPos, Line[] lines, Rectangle2d[] selection)
        {
            this.text = text;
            this.point = point;
            this.isInsert = isInsert;
            this.linesStartPos = linesStartPos;
            this.lines = lines;
            this.selection = selection;
        }

        public int getMousePointPosInText(FontRenderer font, Point point)
        {
            int linePos = point.y / 12;
            if (linePos < 0)
            {
                return 0;
            }
            else if (linePos >= this.lines.length)
            {
                return this.text.length();
            }
            else
            {
                Line line = this.lines[linePos];
                return this.linesStartPos[linePos] + font.getCharacterManager().func_238352_a_(line.lineText, point.x, line.style);
            }
        }

        public int getLineToMove(int cursorPos, int lineAdded)
        {
            int i = getCursorLine(this.linesStartPos, cursorPos);
            int j = i + lineAdded;
            int k;
            if (0 <= j && j < this.linesStartPos.length)
            {
                int l = cursorPos - this.linesStartPos[i];
                int i1 = this.lines[j].lineText.length();
                k = this.linesStartPos[j] + Math.min(l, i1);
            }
            else
            {
                k = cursorPos;
            }

            return k;
        }

        public int getLineStartPos(int cursorPos)
        {
            int i = getCursorLine(this.linesStartPos, cursorPos);
            return this.linesStartPos[i];
        }

        public int getLineEndPos(int cursorPos)
        {
            int i = getCursorLine(this.linesStartPos, cursorPos);
            return this.linesStartPos[i] + this.lines[i].lineText.length();
        }
    }

    static class Line
    {
        private final Style style;
        private final String lineText;
        protected final ITextComponent lineTextComponent;
        protected final int x;
        protected final int y;

        public Line(Style style, String text, int x, int y)
        {
            this.style = style;
            this.lineText = text;
            this.x = x;
            this.y = y;
            this.lineTextComponent = (new StringTextComponent(text)).setStyle(style);
        }
    }

    static class Point
    {
        public final int x;
        public final int y;

        Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
