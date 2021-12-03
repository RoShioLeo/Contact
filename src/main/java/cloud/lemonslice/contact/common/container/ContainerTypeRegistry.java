package cloud.lemonslice.contact.common.container;

import cloud.lemonslice.contact.client.gui.PostboxGui;
import cloud.lemonslice.contact.client.gui.WrappingPaperGui;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cloud.lemonslice.contact.Contact.MODID;

public final class ContainerTypeRegistry
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    public final static RegistryObject<MenuType<WrappingPaperContainer>> WRAPPING_PAPER_CONTAINER = MENU_TYPE_REGISTER.register("wrapping_paper", () -> new MenuType<>((IContainerFactory<WrappingPaperContainer>)(windowId, inv, data) -> new WrappingPaperContainer(windowId, inv, false)));
    public final static RegistryObject<MenuType<PostboxContainer>> RED_POSTBOX_CONTAINER = MENU_TYPE_REGISTER.register("red_postbox", () -> new MenuType<>((IContainerFactory<PostboxContainer>)(windowId, inv, data) -> new PostboxContainer(windowId, inv, true)));
    public final static RegistryObject<MenuType<PostboxContainer>> GREEN_POSTBOX_CONTAINER = MENU_TYPE_REGISTER.register("green_postbox", () -> new MenuType<>((IContainerFactory<PostboxContainer>)(windowId, inv, data) -> new PostboxContainer(windowId, inv, false)));

    public static void clientInit()
    {
        MenuScreens.register(WRAPPING_PAPER_CONTAINER.get(), WrappingPaperGui::new);
        MenuScreens.register(RED_POSTBOX_CONTAINER.get(), PostboxGui::new);
        MenuScreens.register(GREEN_POSTBOX_CONTAINER.get(), PostboxGui::new);
    }
}
