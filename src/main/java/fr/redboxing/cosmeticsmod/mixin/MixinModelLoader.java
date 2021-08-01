package fr.redboxing.cosmeticsmod.mixin;

import com.mojang.datafixers.util.Pair;
import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.Registries;
import fr.redboxing.cosmeticsmod.data.Cosmetic;
import net.fabricmc.fabric.impl.client.model.ModelLoaderHooks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class MixinModelLoader {
    @Shadow @Final private Map<Identifier, Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>> spriteAtlasData;

    @Shadow @Nullable private SpriteAtlasManager spriteAtlasManager;

    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;

    @Shadow @Nullable public abstract BakedModel bake(Identifier id, ModelBakeSettings settings);

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private Map<Identifier, BakedModel> bakedModels;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 3))
    private void onInit(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo ci) {
        for(Cosmetic cosmetic : Registries.COSMETICS) {
            if(!cosmetic.isBuiltin() && cosmetic.getModel() != null) {
                CosmeticsMod.LOGGER.info(cosmetic.getModel().toString());
                ((ModelLoaderHooks) this).fabric_addModel(cosmetic.getModel());
            }
        }
    }
}
