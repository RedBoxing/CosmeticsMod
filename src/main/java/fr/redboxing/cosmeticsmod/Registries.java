package fr.redboxing.cosmeticsmod;

import fr.redboxing.cosmeticsmod.data.Cosmetic;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Registries {
    public static final Registry<Cosmetic> COSMETICS = FabricRegistryBuilder.createSimple(Cosmetic.class, new Identifier("cosmeticsmod", "cosmetics")).buildAndRegister();
}
