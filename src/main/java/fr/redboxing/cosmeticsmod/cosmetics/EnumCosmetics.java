package fr.redboxing.cosmeticsmod.cosmetics;

import fr.redboxing.cosmeticsmod.cosmetics.settings.AbstractCosmeticSettings;
import fr.redboxing.cosmeticsmod.cosmetics.settings.BaseCosmeticSettings;
import fr.redboxing.cosmeticsmod.cosmetics.settings.ColorableBaseCosmeticSettings;

public enum EnumCosmetics {
    DRAGON_WINGS(ColorableBaseCosmeticSettings.class),
    CAPE(BaseCosmeticSettings.class);

    private Class<? extends AbstractCosmeticSettings> settings;

    EnumCosmetics(Class<? extends AbstractCosmeticSettings> settings) {
        this.settings = settings;
    }

    public Class<? extends AbstractCosmeticSettings> getSettings() {
        return settings;
    }
}
