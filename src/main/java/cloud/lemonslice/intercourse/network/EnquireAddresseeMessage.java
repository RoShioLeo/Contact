package cloud.lemonslice.intercourse.network;

import cloud.lemonslice.intercourse.common.capability.MailToBeSent;
import cloud.lemonslice.intercourse.common.container.PostboxContainer;
import cloud.lemonslice.intercourse.common.handler.mail.MailboxManager;
import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;

import static cloud.lemonslice.intercourse.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;

public class EnquireAddresseeMessage implements INormalMessage
{
    private final String nameIn;
    private final BlockPos pos;
    private final RegistryKey<World> world;
    private final boolean isEnder;
    private final boolean shouldSend;

    public EnquireAddresseeMessage(String name, BlockPos pos, RegistryKey<World> world, boolean isEnder, boolean shouldSend)
    {
        this.nameIn = name;
        this.pos = pos;
        this.world = world;
        this.isEnder = isEnder;
        this.shouldSend = shouldSend;
    }

    public EnquireAddresseeMessage(PacketBuffer buf)
    {
        this.nameIn = buf.readString(32767);
        this.pos = buf.readBlockPos();
        this.world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, buf.readResourceLocation());
        this.isEnder = buf.readBoolean();
        this.shouldSend = buf.readBoolean();
    }

    @Override
    public void toBytes(PacketBuffer buf)
    {
        buf.writeString(nameIn, 32767);
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(world.getLocation());
        buf.writeBoolean(isEnder);
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
                            int ticks = 0;
                            if (!isEnder)
                            {
                                if (mailboxPos != null)
                                {
                                    ticks = MailboxManager.getDeliveryTicks(world, pos, mailboxPos.getDimension(), mailboxPos.getPos());
                                }
                                else
                                {
                                    ticks = MailboxManager.getDeliveryTicks(world, pos, World.OVERWORLD, BlockPos.ZERO);
                                }
                            }
                            if (shouldSend)
                            {
                                if (player.openContainer instanceof PostboxContainer)
                                {
                                    PostboxContainer container = ((PostboxContainer) player.openContainer);
                                    ItemStack parcel = container.parcel.getStackInSlot(0);

                                    parcel.getOrCreateTag().putString("Sender", player.getName().getString());

                                    data.PLAYERS_DATA.mailList.add(new MailToBeSent(uuid, parcel, ticks));
                                    SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, -3), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                                    container.parcel.setStackInSlot(0, ItemStack.EMPTY);
                                }
                            }
                            else
                            {
                                SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(name, ticks), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                            }
                            return;
                        }
                    }
                    SimpleNetworkHandler.CHANNEL.sendTo(new AddresseeDataMessage(nameIn, -1), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                });
            });
        }
    }
}
