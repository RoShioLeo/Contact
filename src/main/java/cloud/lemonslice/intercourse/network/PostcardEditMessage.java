package cloud.lemonslice.intercourse.network;

import cloud.lemonslice.intercourse.common.item.PostcardItem;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public PostcardEditMessage(PacketBuffer buf)
    {
        this.postcard = buf.readItemStack();
        this.held = buf.readInt();
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer)
    {
        packetBuffer.writeItemStack(postcard);
        packetBuffer.writeInt(held);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();
        ServerPlayerEntity player = ctx.getSender();
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER && player != null)
        {
            ctx.enqueueWork(() ->
            {
                if (postcard.getItem() instanceof PostcardItem && postcard.hasTag())
                {
                    if (PlayerInventory.isHotbar(held) || held == 40)
                    {
                        ItemStack card = player.inventory.getStackInSlot(held);
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
