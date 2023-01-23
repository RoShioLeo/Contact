package cloud.lemonslice.contact.network;

import cloud.lemonslice.contact.Contact;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class VersionCheckHandler
{
    public static final Identifier VERSION_CHECK = Contact.getIdentifier("version");

    public static void registerServerMessage()
    {
        ServerLoginConnectionEvents.QUERY_START.register(VersionCheckHandler::onLoginQueryStart);
        ServerLoginNetworking.registerGlobalReceiver(VERSION_CHECK, VersionCheckHandler::onServerLoginCheck);
    }

    public static void registerClientMessage()
    {
        ClientLoginNetworking.registerGlobalReceiver(VERSION_CHECK, VersionCheckHandler::onClientLoginQuery);
    }

    private static void onLoginQueryStart(ServerLoginNetworkHandler serverLoginNetworkHandler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer)
    {
        var versionCheck = PacketByteBufs.create();
        sender.sendPacket(VERSION_CHECK, versionCheck);
    }

    private static CompletableFuture<PacketByteBuf> onClientLoginQuery(MinecraftClient client, ClientLoginNetworkHandler clientLoginNetworkHandler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer)
    {
        var response = PacketByteBufs.create();
        response.writeString(Contact.NETWORK_VERSION, 32767);
        return CompletableFuture.completedFuture(response);
    }

    private static void onServerLoginCheck(MinecraftServer server, ServerLoginNetworkHandler handler, boolean responded, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender)
    {
        try
        {
            String version = buf.readString(32767);
            if (!Objects.equals(Contact.NETWORK_VERSION, version))
            {
                handler.disconnect(Text.translatable("message.contact.disconnect.mismatch"));
            }
        }
        catch (Exception ignored)
        {
            handler.disconnect(Text.of("Version is out-of-date. Please update your Contact mod."));
        }
    }
}
