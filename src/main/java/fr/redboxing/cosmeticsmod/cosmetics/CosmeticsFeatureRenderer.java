package fr.redboxing.cosmeticsmod.cosmetics;

import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.Registries;
import fr.redboxing.cosmeticsmod.cosmetics.model.DragonWingsModel;
import fr.redboxing.cosmeticsmod.data.Cosmetic;
import fr.redboxing.cosmeticsmod.user.User;
import fr.redboxing.cosmeticsmod.user.UserManager;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CosmeticsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final Map<Identifier, AnimalModel<AbstractClientPlayerEntity>> builtinModels = new HashMap<>();;
    private final PlayerEntityRenderer renderer;

    public CosmeticsFeatureRenderer(PlayerEntityRenderer renderer, EntityModelLoader loader) {
        super(renderer);

        this.renderer = renderer;
        this.builtinModels.put(new Identifier("cosmeticsmod", "dragon_wings"), new DragonWingsModel(loader.getModelPart(EntityModelLayers.ENDER_DRAGON)));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        User user = UserManager.get(player);
        if(user.getCosmetics() == null || user.getCosmetics().isEmpty()) return;
        if(player.isInvisible()) return;

        user.getCosmetics().forEach(((identifier, data) -> {
            Cosmetic cosmetic = Registries.COSMETICS.get(identifier);
            if(cosmetic != null && data.get("enabled").getAsBoolean()) {
                Color color = new Color(255, 255, 255);
                if(data.get("color").getAsInt() != -1) {
                    int rgb = data.get("color").getAsInt();
                    color = new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
                }

                Identifier parent = cosmetic.getParent();
                matrixStack.push();
                if(cosmetic.isBuiltin()) {
                    AnimalModel<AbstractClientPlayerEntity> model = this.builtinModels.get(identifier);
                    if(model != null) {
                        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(new Identifier(data.get("texture").getAsString())));

                        this.getContextModel().copyStateTo(model);
                        model.setAngles(player, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                        model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, (float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 1.0F);
                    } else {
                        CosmeticsMod.LOGGER.error("Builtin model " + identifier + " is null");
                    }
                } else {
                    BakedModel bakedModel = BakedModelManagerHelper.getModel(MinecraftClient.getInstance().getBakedModelManager(), cosmetic.getModel());
                    if(bakedModel != null) {
                        if(parent != null) {
                            PlayerEntityModel<AbstractClientPlayerEntity> model = this.renderer.getModel();
                            switch (parent.getPath()) {
                                case "head" -> copyTransform(matrixStack, model.head);
                                case "body" -> copyTransform(matrixStack, model.body);
                                case "leftArm" -> copyTransform(matrixStack, model.leftArm);
                                case "rightArm" -> copyTransform(matrixStack, model.rightArm);
                                case "leftLeg" -> copyTransform(matrixStack, model.leftLeg);
                                case "rightLeg" -> copyTransform(matrixStack, model.rightLeg);
                            }
                        }

                        Vec3f translation = cosmetic.getTranslation();
                        Vec3f rotation = cosmetic.getRotation();
                        Vec3f scale = cosmetic.getScale();

                        if(translation != Vec3f.ZERO) {
                            matrixStack.translate(translation.getX(), translation.getY(), translation.getZ());
                        }

                        if(rotation != Vec3f.ZERO) {
                            matrixStack.multiply(new Quaternion(rotation.getX(), rotation.getY(), rotation.getZ(), true));
                        }

                        if(scale != Vec3f.ZERO) {
                            matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());
                        }

                        matrixStack.translate(0.0D, -0.25D, 0.0D);
                        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));

                        renderBakedItemModel(bakedModel, matrixStack, vertexConsumers.getBuffer(RenderLayer.getCutout()), light, OverlayTexture.DEFAULT_UV, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
                    } else {
                        CosmeticsMod.LOGGER.error("bakedModel " + cosmetic.getModel().toString() + " is null !");
                    }
                }

                matrixStack.pop();
            }
        }));
    }

    private void copyTransform(MatrixStack matrixStack, ModelPart modelPart) {
        matrixStack.translate((double)(modelPart.pivotX / 16.0F), (double)(modelPart.pivotY / 16.0F), (double)(modelPart.pivotZ / 16.0F));
        if (modelPart.roll != 0.0F) {
            matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(modelPart.roll));
        }

        if (modelPart.yaw != 0.0F) {
            matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(modelPart.yaw));
        }

        if (modelPart.pitch != 0.0F) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(modelPart.pitch));
        }
    }

    private void renderBakedItemModel(BakedModel model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue) {
        Random random = new Random();
        long l = 42L;
        Direction[] var10 = Direction.values();
        int var11 = var10.length;

        for(int var12 = 0; var12 < var11; ++var12) {
            Direction direction = var10[var12];
            random.setSeed(42L);
            this.renderBakedItemQuads(matrices, vertices, model.getQuads((BlockState)null, direction, random), light, overlay, red, green, blue);
        }

        random.setSeed(42L);
        this.renderBakedItemQuads(matrices, vertices, model.getQuads((BlockState)null, (Direction)null, random), light, overlay, red, green, blue);
    }

    private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, int light, int overlay, float red, float green, float blue) {
        MatrixStack.Entry entry = matrices.peek();

        for(BakedQuad bakedQuad : quads) {
            vertices.quad(entry, bakedQuad, red, green, blue, light, overlay);
        }
    }
}
