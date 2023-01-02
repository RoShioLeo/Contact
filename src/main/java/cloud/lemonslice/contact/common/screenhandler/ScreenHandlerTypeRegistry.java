package cloud.lemonslice.contact.common.screenhandler;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.client.screen.PostboxScreen;
import cloud.lemonslice.contact.client.screen.WrappingPaperScreen;
import cloud.lemonslice.silveroak.common.item.SilveroakRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;

public final class ScreenHandlerTypeRegistry
{
    public final static ScreenHandlerType<WrappingPaperScreenHandler> WRAPPING_PAPER_CONTAINER = SilveroakRegistry.registerScreenHandler(Contact.getIdentifier("wrapping_paper"), (id, inv) -> new WrappingPaperScreenHandler(id, inv, false));
    public final static ScreenHandlerType<PostboxScreenHandler> RED_POSTBOX_CONTAINER = SilveroakRegistry.registerScreenHandler(Contact.getIdentifier("red_postbox"), (id, inv) -> new PostboxScreenHandler(id, inv, true));
    public final static ScreenHandlerType<PostboxScreenHandler> GREEN_POSTBOX_CONTAINER = SilveroakRegistry.registerScreenHandler(Contact.getIdentifier("green_postbox"), (id, inv) -> new PostboxScreenHandler(id, inv, false));

    public static void init()
    {

    }

    public static void clientInit()
    {
        HandledScreens.register(WRAPPING_PAPER_CONTAINER, WrappingPaperScreen::new);
        HandledScreens.register(RED_POSTBOX_CONTAINER, PostboxScreen::new);
        HandledScreens.register(GREEN_POSTBOX_CONTAINER, PostboxScreen::new);
    }
}
