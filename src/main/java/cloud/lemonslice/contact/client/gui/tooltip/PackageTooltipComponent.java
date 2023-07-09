package cloud.lemonslice.contact.client.gui.tooltip;

import cloud.lemonslice.contact.client.item.PackageTooltipData;
import cloud.lemonslice.silveroak.client.texture.TexturePos;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
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
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context)
    {
        int i = this.contents.size();
        for (int m = 0; m < i; ++m)
        {
            int n = x + m * 18 + 1;
            int o = y + 1;
            this.drawSlot(n, o, m, context, textRenderer);
        }
        this.drawOutline(x, y, i, 1, context);
    }

    private void drawSlot(int x, int y, int index, DrawContext context, TextRenderer textRenderer)
    {
        if (index >= 4)
        {
            return;
        }
        ItemStack itemStack = this.contents.get(index);
        this.draw(context, x, y, SLOT);
        context.drawItem(itemStack, x + 1, y + 1, index);
        context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
    }

    private void drawOutline(int x, int y, int columns, int rows, DrawContext context)
    {
        int i;
        this.draw(context, x, y, BORDER_CORNER_TOP);
        this.draw(context, x + columns * 18 + 1, y, BORDER_CORNER_TOP);
        for (i = 0; i < columns; ++i)
        {
            this.draw(context, x + 1 + i * 18, y, BORDER_HORIZONTAL_TOP);
            this.draw(context, x + 1 + i * 18, y + rows * 20, BORDER_HORIZONTAL_BOTTOM);
        }
        for (i = 0; i < rows; ++i)
        {
            this.draw(context, x, y + i * 20 + 1, BORDER_VERTICAL);
            this.draw(context, x + columns * 18 + 1, y + i * 20 + 1, BORDER_VERTICAL);
        }
        this.draw(context, x, y + rows * 20, BORDER_CORNER_BOTTOM);
        this.draw(context, x + columns * 18 + 1, y + rows * 20, BORDER_CORNER_BOTTOM);
    }

    private void draw(DrawContext context, int x, int y, TexturePos pos)
    {
        context.drawTexture(TEXTURE, x, y, 0, pos.getX(), pos.getY(), pos.getWidth(), pos.getHeight(), 128, 128);
    }
}
