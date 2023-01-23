package cloud.lemonslice.contact.client.item;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public record PackageTooltipData(DefaultedList<ItemStack> contents) implements TooltipData
{
    public PackageTooltipData(DefaultedList<ItemStack> contents)
    {
        DefaultedList<ItemStack> list = DefaultedList.of();
        for (ItemStack content : contents)
        {
            if (!content.isEmpty())
            {
                list.add(content);
            }
        }
        this.contents = list;
    }
}
