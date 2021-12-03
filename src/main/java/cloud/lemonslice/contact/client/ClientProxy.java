package cloud.lemonslice.contact.client;

import cloud.lemonslice.contact.client.gui.NewMailToast;
import cloud.lemonslice.contact.client.gui.PostcardEditGui;
import cloud.lemonslice.contact.client.gui.PostcardReadGui;
import cloud.lemonslice.contact.client.renderer.MailboxTileEntityRenderer;
import cloud.lemonslice.contact.common.CommonProxy;
import cloud.lemonslice.contact.common.config.ClientConfig;
import cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;

import static cloud.lemonslice.contact.common.block.BlockRegistry.*;

public class ClientProxy extends CommonProxy
{

    @Override
    public Level getClientWorld()
    {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static void registerRenderType()
    {
        registerCutoutType(ORANGE_MAILBOX.get(), MAGENTA_MAILBOX.get(), LIGHT_BLUE_MAILBOX.get(), YELLOW_MAILBOX.get(),
                LIME_MAILBOX.get(), PINK_MAILBOX.get(), GRAY_MAILBOX.get(), LIGHT_GRAY_MAILBOX.get(),
                CYAN_MAILBOX.get(), PURPLE_MAILBOX.get(), BLUE_MAILBOX.get(), BROWN_MAILBOX.get(),
                GREEN_MAILBOX.get(), RED_MAILBOX.get(), BLACK_MAILBOX.get(), WHITE_MAILBOX.get());
    }

    public static void bindTileEntityRenderer()
    {
        BlockEntityRenderers.register(BlockEntityTypeRegistry.MAILBOX_BLOCK_ENTITY.get(), MailboxTileEntityRenderer::new);
    }

    private static void registerCutoutType(Block... blocks)
    {
        Arrays.asList(blocks).forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout()));
    }

    public static void openPostcardToEdit(ItemStack itemstack, Player playerIn, InteractionHand handIn)
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
