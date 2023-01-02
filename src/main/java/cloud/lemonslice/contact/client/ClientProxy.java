package cloud.lemonslice.contact.client;

import cloud.lemonslice.contact.client.color.block.BlockColorsRegistry;
import cloud.lemonslice.contact.client.color.item.ItemColorsRegistry;
import cloud.lemonslice.contact.client.renderer.MailboxTileEntityRenderer;
import cloud.lemonslice.contact.client.screen.NewMailToast;
import cloud.lemonslice.contact.client.screen.PostcardEditScreen;
import cloud.lemonslice.contact.client.screen.PostcardReadScreen;
import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry;
import cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientProxy implements ClientModInitializer
{
    public static void bindTileEntityRenderer()
    {
        BlockEntityRendererFactories.register(BlockEntityTypeRegistry.MAILBOX_BLOCK_ENTITY, MailboxTileEntityRenderer::new);
    }

    public static void openPostcardToEdit(ItemStack itemstack, PlayerEntity playerIn, Hand handIn)
    {
        MinecraftClient.getInstance().setScreen(new PostcardEditScreen(itemstack, playerIn, handIn));
    }

    public static void openPostcardToRead(ItemStack itemstack)
    {
        MinecraftClient.getInstance().setScreen(new PostcardReadScreen(itemstack));
    }

    public static void notifyNewMail(MinecraftClient client)
    {
        if (ContactConfig.showNewMailToast)
        {
            client.executeSync(() -> client.getToastManager().add(new NewMailToast()));
        }
    }

    @Override
    public void onInitializeClient()
    {
        ScreenHandlerTypeRegistry.clientInit();
        ClientProxy.bindTileEntityRenderer();
        BlockColorsRegistry.init();
        ItemColorsRegistry.init();
        BlockRegistry.registerRenderLayer();
    }

    public static void registerCutoutRenderLayer(Block block)
    {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
    }
}
