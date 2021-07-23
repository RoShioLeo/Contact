package cloud.lemonslice.intercourse.client.renderer;

import cloud.lemonslice.intercourse.common.item.ItemRegistry;
import cloud.lemonslice.intercourse.common.tileentity.MailboxTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import static java.lang.Math.PI;

public class MailboxTileEntityRenderer extends TileEntityRenderer<MailboxTileEntity>
{
    public MailboxTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MailboxTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        if (tileEntityIn.isOpened())
        {
            Minecraft mc = Minecraft.getInstance();
            ItemStack mail = new ItemStack(ItemRegistry.MAIL);
            ItemRenderer renderItem = mc.getItemRenderer();

            matrixStackIn.push();
            matrixStackIn.translate(0.5, 1 + 0.1 * MathHelper.sin((float) (tileEntityIn.getAngel() / 20.0D * PI)), 0.5);
            matrixStackIn.scale(0.6F, 0.6F, 0.6F);
            matrixStackIn.rotate(new Quaternion(Vector3f.YP, -mc.player.rotationYawHead, true));
            RenderHelper.enableStandardItemLighting();
            renderItem.renderItem(mail, ItemCameraTransforms.TransformType.FIXED, 15728880, combinedOverlayIn, matrixStackIn, bufferIn);
            RenderHelper.disableStandardItemLighting();

            matrixStackIn.pop();
        }
    }
}
