package fr.redboxing.cosmeticsmod.mixin;

import fr.redboxing.cosmeticsmod.pack.CosmeticsPackProvider;
import net.minecraft.client.option.GameOptions;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameOptions.class)
public class MixinGameOptions {
    @Shadow
    public List<String> resourcePacks;

    @Inject(method = "load", at = @At("RETURN"))
    private void onLoad(CallbackInfo ci) {
        // Add built-in resource packs if they are enabled by default only if the options file is blank.
       /* if (this.resourcePacks.isEmpty()) {
            List<ResourcePackProfile> profiles = new ArrayList<>();
            CosmeticsPackProvider.COSMETICS_PACK_PROVIDER.register(profiles::add);
            this.resourcePacks = new ArrayList<>();

            for (ResourcePackProfile profile : profiles) {
                ResourcePack pack = profile.createResourcePack();
                if (profile.getSource() == CosmeticsPackProvider.RESOURCE_PACK_SOURCE) {
                    this.resourcePacks.add(profile.getName());
                }
            }
        }*/
    }
}
