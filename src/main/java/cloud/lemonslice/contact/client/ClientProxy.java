package cloud.lemonslice.contact.client;

import cloud.lemonslice.contact.client.color.block.BlockColorsRegistry;
import cloud.lemonslice.contact.client.color.item.ItemColorsRegistry;
import cloud.lemonslice.contact.client.gui.screen.NewMailToast;
import cloud.lemonslice.contact.client.gui.screen.PostcardEditScreen;
import cloud.lemonslice.contact.client.gui.screen.PostcardReadScreen;
import cloud.lemonslice.contact.client.renderer.MailboxTileEntityRenderer;
import cloud.lemonslice.contact.client.renderer.PostcardEntityRenderer;
import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.entity.EntityTypeRegistry;
import cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry;
import cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry;
import cloud.lemonslice.contact.network.NetworkHandler;
import cloud.lemonslice.contact.network.VersionCheckHandler;
import cloud.lemonslice.contact.resourse.PostcardStyle;
import com.google.common.collect.Maps;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.Map;

public class ClientProxy implements ClientModInitializer
{
    private static final Map<PostcardStyle, RenderLayer> CARD_RENDER_LAYERS = Maps.newHashMap();
    private static final Map<PostcardStyle, RenderLayer> POSTMARK_RENDER_LAYERS = Maps.newHashMap();

    public static void bindEntityRenderer()
    {
        BlockEntityRendererFactories.register(BlockEntityTypeRegistry.MAILBOX_BLOCK_ENTITY, MailboxTileEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.POSTCARD, PostcardEntityRenderer::new);
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
        ClientProxy.bindEntityRenderer();
        BlockColorsRegistry.init();
        ItemColorsRegistry.init();
        BlockRegistry.registerRenderLayer();
        NetworkHandler.clientInit();
        VersionCheckHandler.registerClientMessage();
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(new ModelIdentifier("contact", "postcard_pin", "")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(new ModelIdentifier("contact", "postcard", "")));
    }

    public static void registerCutoutRenderLayer(Block block)
    {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
    }

    public static RenderLayer getPostcardCardRenderLayer(PostcardStyle style)
    {
        RenderLayer renderLayer = CARD_RENDER_LAYERS.get(style);
        if (renderLayer == null)
        {
            renderLayer = RenderLayer.getText(style.getCardTexture());
            CARD_RENDER_LAYERS.put(style, renderLayer);
            return renderLayer;
        }
        return renderLayer;
    }

    public static RenderLayer getPostcardPostmarkRenderLayer(PostcardStyle style)
    {
        RenderLayer renderLayer = POSTMARK_RENDER_LAYERS.get(style);
        if (renderLayer == null)
        {
            renderLayer = RenderLayer.getText(style.getPostmarkTexture());
            POSTMARK_RENDER_LAYERS.put(style, renderLayer);
            return renderLayer;
        }
        return renderLayer;
    }
}
