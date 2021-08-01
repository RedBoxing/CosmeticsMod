package fr.redboxing.cosmeticsmod.mixin;

import fr.redboxing.cosmeticsmod.pack.CosmeticsPackProvider;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Consumer;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class MixinClientBuiltinResourcePackProvider {
    @Inject(method = "register", at = @At("RETURN"))
    private void addBuiltinResourcePacks(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory, CallbackInfo ci) {
        // Register mod and built-in resource packs after the vanilla built-in resource packs are registered.
        //CosmeticsPackProvider.COSMETICS_PACK_PROVIDER.register(consumer, factory);
    }
}
