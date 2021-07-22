package cloud.lemonslice.intercourse.common.container;

import cloud.lemonslice.intercourse.client.gui.PostboxGui;
import cloud.lemonslice.intercourse.client.gui.WrappingPaperGui;
import cloud.lemonslice.intercourse.registry.RegistryModule;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;

public final class ContainerTypesRegistry extends RegistryModule
{
    public final static ContainerType<?> WRAPPING_PAPER_CONTAINER = IForgeContainerType.create(((windowId, inv, data) -> new WrappingPaperContainer(windowId, inv, false))).setRegistryName("wrapping_paper");
    public final static ContainerType<?> RED_POSTBOX_CONTAINER = IForgeContainerType.create(((windowId, inv, data) -> new PostboxContainer(windowId, inv, true))).setRegistryName("red_postbox");
    public final static ContainerType<?> GREEN_POSTBOX_CONTAINER = IForgeContainerType.create(((windowId, inv, data) -> new PostboxContainer(windowId, inv, false))).setRegistryName("green_postbox");

    public static void clientInit()
    {
        ScreenManager.registerFactory((ContainerType<WrappingPaperContainer>) WRAPPING_PAPER_CONTAINER, WrappingPaperGui::new);
        ScreenManager.registerFactory((ContainerType<PostboxContainer>) RED_POSTBOX_CONTAINER, PostboxGui::new);
        ScreenManager.registerFactory((ContainerType<PostboxContainer>) GREEN_POSTBOX_CONTAINER, PostboxGui::new);
    }
}
