package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PostcardEditMessage implements INormalMessage
{
    private ItemStack postcard;
    private int held;

    public PostcardEditMessage(ItemStack postcard, int held)
    {
        this.postcard = postcard;
        this.held = held;
    }

    public PostcardEditMessage(FriendlyByteBuf buf)
    {
        this.postcard = buf.readItem();
        this.held = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer)
    {
        packetBuffer.writeItem(postcard);
        packetBuffer.writeInt(held);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();
        ServerPlayer player = ctx.getSender();
        if (player != null)
        {
            ctx.enqueueWork(() ->
            {
                if (postcard.getItem() instanceof PostcardItem && postcard.hasTag())
                {
                    if (Inventory.isHotbarSlot(held) || held == 40)
                    {
                        ItemStack card = player.getInventory().getItem(held);
                        if (card.getItem() instanceof PostcardItem)
                        {
                            card.setTag(postcard.getTag());
                        }
                    }
                }
            });
        }
    }
}
