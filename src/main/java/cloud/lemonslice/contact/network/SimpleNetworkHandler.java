package cloud.lemonslice.contact.network;

import cloud.lemonslice.silveroak.network.INormalMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

import static cloud.lemonslice.contact.Contact.MODID;

public final class SimpleNetworkHandler
{
    public static final String NETWORK_VERSION = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main")).networkProtocolVersion(() -> NETWORK_VERSION).serverAcceptedVersions(NETWORK_VERSION::equals).clientAcceptedVersions(NETWORK_VERSION::equals).simpleChannel();

    public static void init()
    {
        int id = 0;
        registerMessage(id++, ActionMessage.class, ActionMessage::new);
        registerMessage(id++, EnquireAddresseeMessage.class, EnquireAddresseeMessage::new, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(id++, AddresseeDataMessage.class, AddresseeDataMessage::new, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(id++, PostcardEditMessage.class, PostcardEditMessage::new, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(id++, PostcardReloadMessage.class, PostcardReloadMessage::new, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <T extends INormalMessage> void registerMessage(int index, Class<T> messageType, Function<PacketBuffer, T> decoder)
    {
        CHANNEL.registerMessage(index, messageType, INormalMessage::toBytes, decoder, (message, context) ->
        {
            message.process(context);
            context.get().setPacketHandled(true);
        });
    }

    private static <T extends INormalMessage> void registerMessage(int index, Class<T> messageType, Function<PacketBuffer, T> decoder, NetworkDirection direction)
    {
        CHANNEL.registerMessage(index, messageType, INormalMessage::toBytes, decoder, (message, context) ->
        {
            message.process(context);
            context.get().setPacketHandled(true);
        }, Optional.of(direction));
    }
}
