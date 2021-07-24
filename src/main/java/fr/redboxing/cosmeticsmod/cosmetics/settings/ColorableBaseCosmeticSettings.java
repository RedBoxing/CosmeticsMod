package fr.redboxing.cosmeticsmod.cosmetics.settings;

public class ColorableBaseCosmeticSettings extends BaseCosmeticSettings {
    private int[] color;
    private boolean multicolor;

    public ColorableBaseCosmeticSettings(String name, boolean enabled, String texture, int[] color, boolean multicolor) {
        super(name, enabled, texture);
        this.color = color;
        this.multicolor = multicolor;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public boolean isMulticolor() {
        return multicolor;
    }

    public void setMulticolor(boolean multicolor) {
        this.multicolor = multicolor;
    }
}
