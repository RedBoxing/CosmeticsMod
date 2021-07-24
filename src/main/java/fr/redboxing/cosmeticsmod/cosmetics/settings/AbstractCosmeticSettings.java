package fr.redboxing.cosmeticsmod.cosmetics.settings;

public abstract class AbstractCosmeticSettings {
    private String name;
    private boolean enabled;
    private String texture;


    public AbstractCosmeticSettings(String name, boolean enabled, String texture) {
        this.name = name;
        this.enabled = enabled;
        this.texture = texture;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
}


