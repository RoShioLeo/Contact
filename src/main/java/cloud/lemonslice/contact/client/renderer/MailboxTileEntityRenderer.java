package cloud.lemonslice.contact.client.renderer;

import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.tileentity.MailboxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import static java.lang.Math.PI;

public class MailboxTileEntityRenderer implements BlockEntityRenderer<MailboxBlockEntity>
{
    public MailboxTileEntityRenderer(BlockEntityRendererProvider.Context pContext)
    {
    }

    @Override
    public void render(MailboxBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        if (tileEntityIn.isOpened())
        {
            Minecraft mc = Minecraft.getInstance();
            ItemStack mail = new ItemStack(ItemRegistry.MAIL.get());
            ItemRenderer renderItem = mc.getItemRenderer();

            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5, 1 + 0.1 * Mth.sin((float) (tileEntityIn.getAngel() / 20.0D * PI)), 0.5);
            matrixStackIn.scale(0.6F, 0.6F, 0.6F);
            matrixStackIn.mulPose(new Quaternion(Vector3f.YP, -mc.player.yHeadRot, true));

            renderItem.renderStatic(mail, ItemTransforms.TransformType.FIXED, 15728880, combinedOverlayIn, matrixStackIn, bufferIn, 0);

            matrixStackIn.popPose();
        }
    }
}
