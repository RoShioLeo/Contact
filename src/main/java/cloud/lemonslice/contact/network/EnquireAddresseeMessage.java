package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.capability.MailToBeSent;
import cloud.lemonslice.contact.common.container.PostboxContainer;
import cloud.lemonslice.contact.common.handler.AdvancementManager;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;

import static cloud.lemonslice.contact.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;

public class EnquireAddresseeMessage implements INormalMessage
{
    private final String nameIn;
    private final boolean shouldSend;

    public EnquireAddresseeMessage(String name, boolean shouldSend)
    {
        this.nameIn = name;
        this.shouldSend = shouldSend;
    }

    public EnquireAddresseeMessage(PacketBuffer buf)
    {
        this.nameIn = buf.readString(32767);
        this.shouldSend = buf.readBoolean();
    }

    @Override
    public void toBytes(PacketBuffer buf)
    {
        buf.writeString(nameIn, 32767);
        buf.writeBoolean(shouldSend);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();
        ServerPlayerEntity player = ctx.getSender();
        if (player == null)
        {
            return;
        }

        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER)
        {
            ctx.enqueueWork(() ->
            {
                if (nameIn.isEmpty())
                {
                    SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(nameIn, -1), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                    return;
                }
                player.server.getWorld(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
                {
                    String lowerIn = nameIn.toLowerCase(Locale.ROOT);
                    for (String name : data.PLAYERS_DATA.nameToUUID.keySet())
                    {
                        if (name.toLowerCase(Locale.ROOT).startsWith(lowerIn))
                        {
                            UUID uuid = data.PLAYERS_DATA.nameToUUID.get(name);
                            if (data.PLAYERS_DATA.isMailboxFull(uuid))
                            {
                                SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, -2), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                                return;
                            }
                            GlobalPos mailboxPos = data.PLAYERS_DATA.getMailboxPos(uuid);
                            if (player.openContainer instanceof PostboxContainer)
                            {
                                int ticks = 0;
                                if (!((PostboxContainer) player.openContainer).isEnderMail())
                                {
                                    if (mailboxPos != null)
                                    {
                                        ticks = MailboxManager.getDeliveryTicks(player.world.getDimensionKey(), player.getPosition(), mailboxPos.getDimension(), mailboxPos.getPos());
                                    }
                                    else
                                    {
                                        ticks = MailboxManager.getDeliveryTicks(player.world.getDimensionKey(), player.getPosition(), World.OVERWORLD, BlockPos.ZERO);
                                    }
                                }

                                if (shouldSend)
                                {
                                    PostboxContainer container = ((PostboxContainer) player.openContainer);
                                    ItemStack parcel = container.parcel.getStackInSlot(0);
                                    parcel.getOrCreateTag().putString("Sender", player.getName().getString());

                                    if (parcel.getItem() instanceof PostcardItem)
                                    {
                                        AdvancementManager.givePlayerAdvancement(player.server, player, new ResourceLocation("contact:send_postcard"));
                                    }

                                    if (mailboxPos != null)
                                    {
                                        if (mailboxPos.getDimension() != player.world.getDimensionKey())
                                        {
                                            parcel.getOrCreateTag().putBoolean("AnotherWorld", true);
                                        }
                                    }
                                    else
                                    {
                                        if (World.OVERWORLD != player.world.getDimensionKey())
                                        {
                                            parcel.getOrCreateTag().putBoolean("AnotherWorld", true);
                                        }
                                    }

                                    data.PLAYERS_DATA.mailList.add(new MailToBeSent(uuid, parcel, ticks));
                                    SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, -3), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                                    container.parcel.setStackInSlot(0, ItemStack.EMPTY);
                                }
                                else
                                {
                                    SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, ticks), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                                }
                                return;
                            }
                        }
                    }
                    SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(nameIn, -1), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                });
            });
        }
    }
}
