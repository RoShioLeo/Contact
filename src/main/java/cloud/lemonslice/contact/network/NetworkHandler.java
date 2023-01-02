package cloud.lemonslice.contact.network;

import static cloud.lemonslice.silveroak.network.SimpleNetworkHandler.registerToClientMessage;
import static cloud.lemonslice.silveroak.network.SimpleNetworkHandler.registerToServerMessage;

public final class NetworkHandler
{
    public static void init()
    {
        registerToClientMessage(PostcardReloadMessage.getID(), PostcardReloadMessage::fromBytes);
        registerToClientMessage(AddresseeDataMessage.getID(), AddresseeDataMessage::fromBytes);
        registerToClientMessage(ActionMessage.getID(), ActionMessage::fromBytes);

        registerToServerMessage(PostcardEditMessage.getID(), PostcardEditMessage::fromBytes);
        registerToServerMessage(ActionMessage.getID(), ActionMessage::fromBytes);
        registerToServerMessage(EnquireAddresseeMessage.getID(), EnquireAddresseeMessage::fromBytes);
    }
}
