package cloud.lemonslice.contact.client.renderer;

import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.tileentity.MailboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static java.lang.Math.PI;

public class MailboxTileEntityRenderer implements BlockEntityRenderer<MailboxBlockEntity>
{
    private final ItemRenderer itemRenderer;


    public MailboxTileEntityRenderer(BlockEntityRendererFactory.Context context)
    {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MailboxBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (tileEntityIn.isOpened())
        {
            ItemStack mail = new ItemStack(ItemRegistry.MAIL);

            matrixStackIn.push();
            matrixStackIn.translate(0.5, 1 + 0.1 * MathHelper.sin((float) (tileEntityIn.getAngel() / 20.0D * PI)), 0.5);
            matrixStackIn.scale(0.6F, 0.6F, 0.6F);
            matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotation(-MinecraftClient.getInstance().player.headYaw * ((float) Math.PI / 180F)));

            itemRenderer.renderItem(mail, ModelTransformation.Mode.FIXED, 15728880, overlay, matrixStackIn, vertexConsumers, 0);

            matrixStackIn.pop();
        }
    }
}
