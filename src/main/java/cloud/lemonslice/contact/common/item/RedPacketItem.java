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
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static cloud.lemonslice.contact.Contact.MODID;

public class RedPacketItem extends NormalItem implements IMailItem, IPackageItem
{

    public RedPacketItem()
    {
        super(new Identifier(MODID, "red_packet"), new FabricItemSettings().maxCount(1), Contact.ITEM_GROUP);
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
        if (stack.getOrCreateNbt().contains("blessing"))
        {
            tooltip.add(Text.translatable("tooltip.contact.red_packet.blessing", stack.getOrCreateNbt().getString("blessing")).formatted(Formatting.GRAY));
        }
        this.addSenderInfoTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean isEnderType()
    {
        return false;
    }

    public static ItemStack getRedPacket(SimpleInventory contents, String blessings, String sender)
    {
        ItemStack letter = new ItemStack(ItemRegistry.RED_PACKET);
        letter.getOrCreateNbt().put("parcel", contents.toNbtList());
        letter.getOrCreateNbt().putString("blessings", blessings);
        if (!sender.isEmpty())
        {
            letter.getOrCreateNbt().putString("Sender", sender);
        }
        return letter;
    }

    @Override
    public int getCapacity()
    {
        return 1;
    }
}
