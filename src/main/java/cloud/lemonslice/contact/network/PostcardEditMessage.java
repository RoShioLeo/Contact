package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.silveroak.network.IToServerMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static cloud.lemonslice.contact.Contact.MODID;

public class PostcardEditMessage implements IToServerMessage
{
    private ItemStack postcard;
    private int held;

    public PostcardEditMessage(ItemStack postcard, int held)
    {
        this.postcard = postcard;
        this.held = held;
    }

    @Override
    public PacketByteBuf toBytes()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(postcard);
        buf.writeInt(held);
        return buf;
    }

    @Override
    public void process(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketSender responseSender)
    {
        if (player != null)
        {
            server.executeSync(() ->
            {
                if (postcard.getItem() instanceof PostcardItem && postcard.hasNbt())
                {
                    if (PlayerInventory.isValidHotbarIndex(held) || held == 40)
                    {
                        ItemStack card = player.getInventory().getStack(held);
                        if (card.getItem() instanceof PostcardItem)
                        {
                            card.setNbt(postcard.getNbt());
                        }
                    }
                }
            });
        }
    }

    public static Identifier getID()
    {
        return new Identifier(MODID, "postcard_edit");
    }

    public static PostcardEditMessage fromBytes(PacketByteBuf buf)
    {
        ItemStack postcard = buf.readItemStack();
        int held = buf.readInt();
        return create(postcard, held);
    }

    public static PostcardEditMessage create(ItemStack postcard, int held)
    {
        return new PostcardEditMessage(postcard, held);
    }
}
