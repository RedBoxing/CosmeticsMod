package fr.redboxing.cosmeticsmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ResourceReloadLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface IMinecraftClient {
    @Accessor("resourceReloadLogger")
    ResourceReloadLogger getResourceReloadLogger();
}
