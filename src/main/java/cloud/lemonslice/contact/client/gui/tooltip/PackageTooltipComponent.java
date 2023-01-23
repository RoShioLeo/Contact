package cloud.lemonslice.contact.client.gui.tooltip;

import cloud.lemonslice.contact.client.item.PackageTooltipData;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class PackageTooltipComponent implements TooltipComponent
{
    public static final Identifier TEXTURE = BundleTooltipComponent.TEXTURE;
    private final DefaultedList<ItemStack> contents;

    private static final TexturePos SLOT = TexturePos.create(0, 0, 18, 20);
    private static final TexturePos BORDER_VERTICAL = TexturePos.create(0, 18, 1, 20);
    private static final TexturePos BORDER_HORIZONTAL_TOP = TexturePos.create(0, 20, 18, 1);
    private static final TexturePos BORDER_HORIZONTAL_BOTTOM = TexturePos.create(0, 60, 18, 1);
    private static final TexturePos BORDER_CORNER_TOP = TexturePos.create(0, 20, 1, 1);
    private static final TexturePos BORDER_CORNER_BOTTOM = TexturePos.create(0, 60, 1, 1);

    public PackageTooltipComponent(PackageTooltipData data)
    {
        contents = data.contents();
    }

    @Override
    public int getHeight()
    {
        return 26;
    }

    @Override
    public int getWidth(TextRenderer textRenderer)
    {
        return this.contents.size() * 18 + 2;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z)
    {
        int i = this.contents.size();
        for (int m = 0; m < i; ++m)
        {
            int n = x + m * 18 + 1;
            int o = y + 1;
            this.drawSlot(n, o, m, textRenderer, matrices, itemRenderer, z);
        }
        this.drawOutline(x, y, i, 1, matrices, z);
    }

    private void drawSlot(int x, int y, int index, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z)
    {
        if (index >= 4)
        {
            return;
        }
        ItemStack itemStack = this.contents.get(index);
        this.draw(matrices, x, y, z, SLOT);
        itemRenderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1, index);
        itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1);
    }

    private void drawOutline(int x, int y, int columns, int rows, MatrixStack matrices, int z)
    {
        int i;
        this.draw(matrices, x, y, z, BORDER_CORNER_TOP);
        this.draw(matrices, x + columns * 18 + 1, y, z, BORDER_CORNER_TOP);
        for (i = 0; i < columns; ++i)
        {
            this.draw(matrices, x + 1 + i * 18, y, z, BORDER_HORIZONTAL_TOP);
            this.draw(matrices, x + 1 + i * 18, y + rows * 20, z, BORDER_HORIZONTAL_BOTTOM);
        }
        for (i = 0; i < rows; ++i)
        {
            this.draw(matrices, x, y + i * 20 + 1, z, BORDER_VERTICAL);
            this.draw(matrices, x + columns * 18 + 1, y + i * 20 + 1, z, BORDER_VERTICAL);
        }
        this.draw(matrices, x, y + rows * 20, z, BORDER_CORNER_BOTTOM);
        this.draw(matrices, x + columns * 18 + 1, y + rows * 20, z, BORDER_CORNER_BOTTOM);
    }

    private void draw(MatrixStack matrices, int x, int y, int z, TexturePos pos)
    {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, x, y, z, pos.getX(), pos.getY(), pos.getWidth(), pos.getHeight(), 128, 128);
    }
}
