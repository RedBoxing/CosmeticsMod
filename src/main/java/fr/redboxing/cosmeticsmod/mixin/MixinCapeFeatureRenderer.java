package fr.redboxing.cosmeticsmod.mixin;

import fr.redboxing.cosmeticsmod.cosmetics.PlayerHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeFeatureRenderer.class)
public class MixinCapeFeatureRenderer {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        PlayerHandler playerHandler = PlayerHandler.get(abstractClientPlayerEntity);
        if(!playerHandler.canRenderMojangCape(abstractClientPlayerEntity) || abstractClientPlayerEntity.getCapeTexture() == null) ci.cancel();
    }
}
