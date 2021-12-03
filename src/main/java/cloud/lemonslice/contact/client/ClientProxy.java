package cloud.lemonslice.contact.client;

import cloud.lemonslice.contact.client.gui.NewMailToast;
import cloud.lemonslice.contact.client.gui.PostcardEditGui;
import cloud.lemonslice.contact.client.gui.PostcardReadGui;
import cloud.lemonslice.contact.client.renderer.MailboxTileEntityRenderer;
import cloud.lemonslice.contact.common.CommonProxy;
import cloud.lemonslice.contact.common.config.ClientConfig;
import cloud.lemonslice.contact.common.tileentity.TileEntityTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Arrays;

import static cloud.lemonslice.contact.common.block.BlockRegistry.*;

public class ClientProxy extends CommonProxy
{

    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().level;
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
        ClientRegistry.bindTileEntityRenderer(TileEntityTypeRegistry.MAILBOX, MailboxTileEntityRenderer::new);
    }

    private static void registerCutoutType(Block... blocks)
    {
        Arrays.asList(blocks).forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
    }

    public static void openPostcardToEdit(ItemStack itemstack, PlayerEntity playerIn, Hand handIn)
    {
        Minecraft.getInstance().setScreen(new PostcardEditGui(itemstack, playerIn, handIn));
    }

    public static void openPostcardToRead(ItemStack itemstack)
    {
        Minecraft.getInstance().setScreen(new PostcardReadGui(itemstack));
    }

    public static void notifyNewMail(NetworkEvent.Context ctx)
    {
        if (ClientConfig.GUI.showNewMailToast.get())
        {
            ctx.enqueueWork(() -> Minecraft.getInstance().getToasts().addToast(new NewMailToast()));
        }
    }
}
