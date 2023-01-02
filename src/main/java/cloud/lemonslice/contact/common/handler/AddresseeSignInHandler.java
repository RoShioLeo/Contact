package cloud.lemonslice.contact.common.handler;

import cloud.lemonslice.contact.common.storage.MailboxDataStorage;
import cloud.lemonslice.contact.network.ActionMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.UUID;

import static cloud.lemonslice.contact.common.handler.MailboxManager.updateState;

public final class AddresseeSignInHandler
{
    public static void onPlayerLoggedIn(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server)
    {
        MailboxDataStorage data = MailboxDataStorage.getMailboxData(server);

        UUID uuid = handler.getPlayer().getUuid();
        data.getData().nameToUUID.put(handler.getPlayer().getName().getString(), uuid);
        if (data.getData().uuidToContents.get(uuid) == null)
        {
            data.getData().resetMailboxContents(uuid);
        }
        else
        {
            if (!data.getData().isMailboxEmpty(uuid))
            {
                ServerPlayNetworking.send(handler.getPlayer(), ActionMessage.getID(), ActionMessage.create(0).toBytes());
            }
            updateState(uuid, data.getData());
        }
    }
}
