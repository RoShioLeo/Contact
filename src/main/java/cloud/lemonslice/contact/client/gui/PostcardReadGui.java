package cloud.lemonslice.contact.client.gui;

import cloud.lemonslice.silveroak.client.texture.TexturePos;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import cloud.lemonslice.silveroak.helper.GuiHelper;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.Style;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

import static cloud.lemonslice.contact.Contact.MODID;

public class PostcardReadGui extends Screen
{
    private String page = "";
    private final int posX;
    private final int posY;
    private final int textWidth;
    private final int color;
    private final ResourceLocation texture;
    private static final ResourceLocation POSTMARK = new ResourceLocation(MODID, "textures/postcard/postmark.png");

    private PostcardEditGui.Page currentPage = PostcardEditGui.Page.EMPTY;

    public PostcardReadGui(ItemStack postcardIn)
    {
        super(NarratorChatListener.EMPTY);
        CompoundNBT compoundnbt = postcardIn.getTag();
        String id;
        if (compoundnbt != null)
        {
            INBT nbt = compoundnbt.get("Text");
            if (nbt != null)
            {
                this.page = nbt.copy().getString();
            }

            CompoundNBT compoundNBT = compoundnbt.getCompound("Info");
            id = compoundNBT.getString("ID");
            this.posX = compoundNBT.getInt("PosX");
            this.posY = compoundNBT.getInt("PosY");
            this.textWidth = compoundNBT.getInt("Width");
            this.color = compoundNBT.getInt("Color");
        }
        else
        {
            id = "stripes";
            this.posX = 10;
            this.posY = 12;
            this.textWidth = 180;
            this.color = 0xff4d4d4d;
        }
        this.texture = new ResourceLocation(MODID, "textures/postcard/" + id + ".png");
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
            charactermanager.func_238353_a_(page, textWidth, Style.EMPTY, true, (style, lineStartPos, lineEndPos) ->
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
        return new PostcardEditGui.Point(pointIn.x + (this.width - 200) / 2 + posX, pointIn.y + posY + 50);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.setListener(null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiHelper.drawLayer(matrixStack, (this.width - 200) / 2, 50, texture, new TexturePos(0, 0, 200, 133));

        RenderSystem.enableBlend();
        RenderSystem.color4f(ColorHelper.getRedF(color), ColorHelper.getGreenF(color), ColorHelper.getBlueF(color), 0.8F);
        GuiHelper.drawLayer(matrixStack, (this.width - 200) / 2 + 142, 45, POSTMARK, new TexturePos(0, 0, 64, 52));
        RenderSystem.disableBlend();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        for (PostcardEditGui.Line line : currentPage.lines)
        {
            this.font.drawText(matrixStack, line.lineTextComponent, (float) line.x, (float) line.y, color);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
