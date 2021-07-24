package fr.redboxing.cosmeticsmod.cosmetics;

import fr.redboxing.cosmeticsmod.cosmetics.model.DragonWingsModel;
import fr.redboxing.cosmeticsmod.cosmetics.settings.ColorableBaseCosmeticSettings;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CosmeticsLayer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private Map<EnumCosmetics, AnimalModel<T>> cosmeticsModels;

    public CosmeticsLayer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);

        this.cosmeticsModels = new HashMap<>();
        this.cosmeticsModels.put(EnumCosmetics.DRAGON_WINGS, new DragonWingsModel<>(loader.getModelPart(EntityModelLayers.ENDER_DRAGON)));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            PlayerHandler handler = PlayerHandler.get(player);

            if(handler.getCosmetics() == null) return;

            //matrixStack.push();
            //matrixStack.translate(0.0D, 0.0D, 0.125D);

            handler.getCosmetics().forEach((type, settings) -> {
                if(settings.isEnabled() && this.cosmeticsModels.containsKey(type)) {
                    AnimalModel<T> model = this.cosmeticsModels.get(type);
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(new Identifier(settings.getTexture())));

                    Color color = new Color(255, 255, 255);
                    if(settings instanceof ColorableBaseCosmeticSettings) {
                        ColorableBaseCosmeticSettings colorableSettings = (ColorableBaseCosmeticSettings) settings;
                        color = new Color(colorableSettings.getColor()[0], colorableSettings.getColor()[1], colorableSettings.getColor()[2]);
                        if (colorableSettings.isMulticolor()) {
                            color = Color.getHSBColor((System.currentTimeMillis() % 10000) / 5000F, 0.8F, 1F);
                        }
                    }

                    this.getContextModel().copyStateTo(model);
                    model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                    model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, (float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 1.0F);
                }
            });

           // matrixStack.pop();
        }
    }
}
