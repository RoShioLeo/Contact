package cloud.lemonslice.intercourse.client;

import cloud.lemonslice.intercourse.client.renderer.MailboxTileEntityRenderer;
import cloud.lemonslice.intercourse.common.CommonProxy;
import cloud.lemonslice.intercourse.common.tileentity.TileEntityTypesRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Arrays;

import static cloud.lemonslice.intercourse.common.block.BlocksRegistry.*;

public class ClientProxy extends CommonProxy
{

    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static void registerRenderType()
    {
        registerCutoutType(ORANGE_MAILBOX, MAGENTA_MAILBOX, LIGHT_BLUE_MAILBOX, YELLOW_MAILBOX,
                LIME_MAILBOX, PINK_MAILBOX, GRAY_MAILBOX, LIGHT_GRAY_MAILBOX,
                CYAN_MAILBOX, PURPLE_MAILBOX, BLUE_MAILBOX, BROWN_MAILBOX,
                GREEN_MAILBOX, RED_MAILBOX, BLACK_MAILBOX, WHITE_MAILBOX);
    }

    public static void bindTileEntityRenderer()
    {
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesRegistry.MAILBOX, MailboxTileEntityRenderer::new);
    }

    private static void registerCutoutType(Block... blocks)
    {
        Arrays.asList(blocks).forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutout()));
    }
}
