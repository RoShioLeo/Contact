package cloud.lemonslice.contact.client.renderer;

import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.contact.common.entity.PostcardEntity;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.resourse.PostcardStyle;
import cloud.lemonslice.silveroak.helper.ColorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;

import java.util.List;

public class PostcardEntityRenderer<T extends PostcardEntity> extends EntityRenderer<T>
{
    private static final ModelIdentifier PIN = new ModelIdentifier("contact", "postcard_pin", "");
    private static final ModelIdentifier POSTCARD = new ModelIdentifier("contact", "postcard", "");
    private final BlockRenderManager blockRenderManager;
    private final List<String> list = Lists.newArrayList();
    private int textHash = 0;

    public PostcardEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light)
    {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
        matrixStack.push();
        Direction direction = entity.getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(entity, tickDelta);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate((double) direction.getOffsetX() * d, (double) direction.getOffsetY() * d, (double) direction.getOffsetZ() * d);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - entity.getYaw()));
        boolean bl = entity.isInvisible();
        ItemStack postcard = entity.getPostcard();
        if (!postcard.isEmpty())
        {
            PostcardStyle postcardStyle = PostcardStyle.fromNBT(postcard.getOrCreateNbt());
            float width = postcardStyle.cardWidth / 2.0f;
            float height = postcardStyle.cardHeight / 2.0f;

            int j = entity.getRotation();
            if (direction.getId() > 1)
            {
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) ((j + 1) % 3 - 1) * 360.0f / 16.0f));
            }
            else
            {
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) j * 360.0f / 16.0f));
            }

            BakedModelManager bakedModelManager = this.blockRenderManager.getModels().getModelManager();
            if (!bl && direction.getId() > 1)
            {
                float red = ColorHelper.getRedF(postcardStyle.postmarkColor);
                float green = ColorHelper.getGreenF(postcardStyle.postmarkColor);
                float blue = ColorHelper.getBlueF(postcardStyle.postmarkColor);
                matrixStack.push();
                matrixStack.translate(-0.5f, -0.5f, -0.5f);
                matrixStack.translate(0.0f, -(128 - height) / 256.0f, 0.0f);
                this.blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()), null, bakedModelManager.getModel(PIN), red, green, blue, light, OverlayTexture.DEFAULT_UV);
                matrixStack.pop();
            }

            matrixStack.push();
            matrixStack.scale(width / 128.0f, height / 128.0f, 1.0f);
            matrixStack.translate(-0.5f, -0.5f, -0.5f);
            this.blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()), null, bakedModelManager.getModel(POSTCARD), 1.0f, 1.0f, 1.0f, light, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();

            matrixStack.translate(0.0f, 0.0f, 0.5f);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
            matrixStack.translate(-64.0f, -64.0f, 0.0f);
            matrixStack.translate(0.0f, 0.0f, -1.0f);

            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(ClientProxy.getPostcardCardRenderLayer(postcardStyle));

            float pointX0 = 64.0f - width / 2.0f;
            float pointX1 = 64.0f + width / 2.0f;
            float pointY0 = 64.0f - height / 2.0f;
            float pointY1 = 64.0f + height / 2.0f;

            vertexConsumer.vertex(matrix4f, pointX0, pointY1, -0.01f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, pointX1, pointY1, -0.01f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, pointX1, pointY0, -0.01f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, pointX0, pointY0, -0.01f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(light).next();

            if (postcard.getOrCreateNbt().contains("Sender"))
            {
                float markX0 = pointX0 + postcardStyle.postmarkPosX / 2.0f;
                float markY0 = pointY0 + postcardStyle.postmarkPosY / 2.0f;

                VertexConsumer vertex = vertexConsumerProvider.getBuffer(ClientProxy.getPostcardPostmarkRenderLayer(postcardStyle));

                float markWidth = postcardStyle.postmarkWidth / 2.0f;
                float markHeight = postcardStyle.postmarkHeight / 2.0f;

                int red = ColorHelper.getRed(postcardStyle.postmarkColor);
                int green = ColorHelper.getGreen(postcardStyle.postmarkColor);
                int blue = ColorHelper.getBlue(postcardStyle.postmarkColor);
                int alpha = ColorHelper.getAlpha(postcardStyle.postmarkColor);

                vertex.vertex(matrix4f, markX0, markY0 + markHeight, -0.02f).color(red, green, blue, alpha).texture(0.0f, 1.0f).light(light).next();
                vertex.vertex(matrix4f, markX0 + markWidth, markY0 + markHeight, -0.02f).color(red, green, blue, alpha).texture(1.0f, 1.0f).light(light).next();
                vertex.vertex(matrix4f, markX0 + markWidth, markY0, -0.02f).color(red, green, blue, alpha).texture(1.0f, 0.0f).light(light).next();
                vertex.vertex(matrix4f, markX0, markY0, -0.02f).color(red, green, blue, alpha).texture(0.0f, 0.0f).light(light).next();
            }

            String text = PostcardItem.getText(postcard);
            if (!text.isBlank())
            {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                float textX0 = pointX0 + postcardStyle.textPosX / 2.0f;
                float textY0 = pointY0 + postcardStyle.textPosY / 2.0f;

                if (textHash != text.hashCode())
                {
                    list.clear();
                    textRenderer.getTextHandler().wrapLines(text, postcardStyle.textWidth, Style.EMPTY, true, (style, lineStartPos, lineEndPos) ->
                    {
                        String lineTextRaw = text.substring(lineStartPos, lineEndPos);
                        String lineText = StringUtils.stripEnd(lineTextRaw, " \n");
                        list.add(lineText);
                    });
                    textHash = text.hashCode();
                }

                matrixStack.push();
                matrixStack.translate(textX0, textY0, -0.025f);
                matrixStack.scale(0.5f, 0.5f, 1.0f);
                matrixStack.translate(0.0f, 0.0f, -0.1f);
                for (String t : list)
                {
                    textRenderer.draw(t, 0.0f, 0.0f, postcardStyle.textColor, false, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, false, 0, light);
                    matrixStack.translate(0.0f, 12.0f, 0.0f);
                }
                matrixStack.pop();
            }
        }
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(T entity)
    {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public Vec3d getPositionOffset(T entity, float f)
    {
        return new Vec3d((float) entity.getHorizontalFacing().getOffsetX() * 0.3f, -0.25, (float) entity.getHorizontalFacing().getOffsetZ() * 0.3f);
    }

    @Override
    protected boolean hasLabel(T entity)
    {
        if (!MinecraftClient.isHudEnabled() || entity.getPostcard().isEmpty() || !entity.getPostcard().hasCustomName() || this.dispatcher.targetedEntity != entity)
        {
            return false;
        }
        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        float f = entity.isSneaky() ? 32.0f : 64.0f;
        return d < (double) (f * f);
    }
}
