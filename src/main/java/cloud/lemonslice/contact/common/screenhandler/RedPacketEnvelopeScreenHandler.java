package cloud.lemonslice.contact.common.screenhandler;

import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.item.IPackageItem;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry.RED_PACKET_ENVELOPE_CONTAINER;

public class RedPacketEnvelopeScreenHandler extends PackageScreenHandler
{
    public final static int CONTENT_COUNT = 1;
    public final SimpleInventory inputs = new SimpleInventory(CONTENT_COUNT);
    public String blessings = "";

    public RedPacketEnvelopeScreenHandler(int id, Inventory inv)
    {
        super(RED_PACKET_ENVELOPE_CONTAINER, id);
        addSlot(new Slot(inputs, 0, 40, 34)
        {
            @Override
            public boolean canInsert(ItemStack stack)
            {
                if (stack.getItem() instanceof IPackageItem)
                    return false;
                Identifier id = Registries.ITEM.getId(stack.getItem());
                return !ContactConfig.blacklistID.contains(id.toString()) && !ContactConfig.blacklistID.contains(id.getPath());
            }
        });
        for (int i = 0; i < 3; ++i)
        {

            for (int j = 0; j < 9; ++j)
            {
                addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 67 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            addSlot(new Slot(inv, i, 8 + i * 18, 125));
        }
    }

    @Override
    public SimpleInventory getInventory()
    {
        return inputs;
    }

    @Override
    public ItemStack getUnpackedItem()
    {
        return new ItemStack(ItemRegistry.RED_PACKET_ENVELOPE);
    }

    @Override
    public ItemStack getPackedItem()
    {
        ItemStack redPacket = new ItemStack(ItemRegistry.RED_PACKET);
        if (!blessings.isBlank())
        {
            redPacket.getOrCreateNbt().putString("blessing", blessings);
        }
        return redPacket;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public int getContainerCount()
    {
        return CONTENT_COUNT;
    }
}
