package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.common.capability.MailToBeSent;
import cloud.lemonslice.contact.common.container.PostboxContainer;
import cloud.lemonslice.contact.common.handler.AdvancementManager;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;

import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;

public class EnquireAddresseeMessage implements INormalMessage
{
    private final String nameIn;
    private final boolean shouldSend;

    public EnquireAddresseeMessage(String name, boolean shouldSend)
    {
        this.nameIn = name;
        this.shouldSend = shouldSend;
    }

    public EnquireAddresseeMessage(FriendlyByteBuf buf)
    {
        this.nameIn = buf.readUtf(32767);
        this.shouldSend = buf.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeUtf(nameIn, 32767);
        buf.writeBoolean(shouldSend);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();
        ServerPlayer player = ctx.getSender();
        if (player == null)
        {
            return;
        }

        ctx.enqueueWork(() ->
        {
            if (nameIn.isEmpty())
            {
                SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(nameIn, -1), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                return;
            }
            player.server.getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
            {
                String lowerIn = nameIn.toLowerCase(Locale.ROOT);
                if (lowerIn.equals("@e") && player.server.getProfilePermissions(player.getGameProfile()) >= 2)
                {
                    if (shouldSend)
                    {
                        if (player.containerMenu instanceof PostboxContainer)
                        {
                            PostboxContainer container = ((PostboxContainer) player.containerMenu);
                            ItemStack parcel = container.parcel.getStackInSlot(0).copy();
                            parcel.getOrCreateTag().putString("Sender", player.getName().getString());
                            for (UUID uuid : data.getData().nameToUUID.values())
                            {
                                data.getData().mailList.add(new MailToBeSent(uuid, parcel.copy(), 0));
                            }
                            SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(lowerIn, -3), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                            container.parcel.setStackInSlot(0, ItemStack.EMPTY);
                        }
                        return;
                    }
                    else
                    {
                        SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(lowerIn, 0), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                        return;
                    }
                }
                for (String name : data.getData().nameToUUID.keySet())
                {
                    if (name.toLowerCase(Locale.ROOT).startsWith(lowerIn))
                    {
                        UUID uuid = data.getData().nameToUUID.get(name);
                        if (data.getData().isMailboxFull(uuid))
                        {
                            SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, -2), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                            return;
                        }
                        GlobalPos mailboxPos = data.getData().getMailboxPos(uuid);
                        if (player.containerMenu instanceof PostboxContainer)
                        {
                            int ticks = 0;
                            if (!((PostboxContainer) player.containerMenu).isEnderMail())
                            {
                                if (mailboxPos != null)
                                {
                                    ticks = MailboxManager.getDeliveryTicks(player.level.dimension(), player.blockPosition(), mailboxPos.dimension(), mailboxPos.pos());
                                }
                                else
                                {
                                    ticks = MailboxManager.getDeliveryTicks(player.level.dimension(), player.blockPosition(), Level.OVERWORLD, BlockPos.ZERO);
                                }
                            }

                            if (shouldSend)
                            {
                                PostboxContainer container = ((PostboxContainer) player.containerMenu);
                                ItemStack parcel = container.parcel.getStackInSlot(0);
                                parcel.getOrCreateTag().putString("Sender", player.getName().getString());

                                if (parcel.getItem() instanceof PostcardItem)
                                {
                                    AdvancementManager.givePlayerAdvancement(player.server, player, new ResourceLocation("contact:send_postcard"));
                                }

                                if (mailboxPos != null)
                                {
                                    if (mailboxPos.dimension() != player.level.dimension())
                                    {
                                        parcel.getOrCreateTag().putBoolean("AnotherWorld", true);
                                    }
                                }
                                else
                                {
                                    if (Level.OVERWORLD != player.level.dimension())
                                    {
                                        parcel.getOrCreateTag().putBoolean("AnotherWorld", true);
                                    }
                                }

                                data.getData().mailList.add(new MailToBeSent(uuid, parcel, ticks));
                                SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, -3), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                                container.parcel.setStackInSlot(0, ItemStack.EMPTY);
                            }
                            else
                            {
                                SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, ticks), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                            }
                            return;
                        }
                    }
                }
                SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(nameIn, -1), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            });
        });
    }
}
