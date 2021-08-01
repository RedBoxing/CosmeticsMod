package fr.redboxing.cosmeticsmod.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.redboxing.cosmeticsmod.pack.CosmeticsPackProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Set;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
   /* @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackManager;<init>(Lnet/minecraft/resource/ResourcePackProfile$Factory;[Lnet/minecraft/resource/ResourcePackProvider;)V"), index = 1)
    private ResourcePackProvider[] modifyResourcePackManager(ResourcePackProvider... providers) {
        Set<ResourcePackProvider> copy = Sets.newHashSet(providers);
        copy.add(CosmeticsPackProvider.COSMETICS_PACK_PROVIDER);
        return copy.toArray(new ResourcePackProvider[0]);
    }*/
}
