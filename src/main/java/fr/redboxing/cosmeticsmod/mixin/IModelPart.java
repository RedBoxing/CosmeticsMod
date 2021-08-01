package fr.redboxing.cosmeticsmod.mixin;

import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelPart.class)
public interface IModelPart {
    @Accessor("children")
    Map<String, ModelPart> getChildren();
}
