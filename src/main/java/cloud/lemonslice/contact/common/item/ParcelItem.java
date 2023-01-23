package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.client.item.PackageTooltipData;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static cloud.lemonslice.contact.Contact.MODID;

public class ParcelItem extends NormalItem implements IMailItem, IPackageItem
{
    private final boolean isEnderType;

    public ParcelItem(String id, boolean isEnderType)
    {
        super(new Identifier(MODID, id), new FabricItemSettings().maxCount(1), Contact.ITEM_GROUP);
        this.isEnderType = isEnderType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        IPackageItem.openPackage(this, user, hand);
        return TypedActionResult.success(ItemStack.EMPTY);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack)
    {
        PackageTooltipData data = IPackageItem.getTooltipData(this, stack);
        return data.contents().isEmpty() ? Optional.empty() : Optional.of(data);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        this.addSenderInfoTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean isEnderType()
    {
        return isEnderType;
    }

    public static ItemStack getParcel(SimpleInventory contents, boolean isEnderType, String sender)
    {
        ItemStack parcel = new ItemStack(isEnderType ? ItemRegistry.ENDER_PARCEL : ItemRegistry.PARCEL);
        parcel.getOrCreateNbt().put("parcel", contents.toNbtList());
        if (!sender.isEmpty())
        {
            parcel.getOrCreateNbt().putString("Sender", sender);
        }
        return parcel;
    }

    @Override
    public int getCapacity()
    {
        return 4;
    }
}
