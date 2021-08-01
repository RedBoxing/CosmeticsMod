package fr.redboxing.cosmeticsmod.data;

import com.google.gson.JsonObject;
import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.Registries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import lombok.Getter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class Cosmetic {
    public static final SerializableData DATA = new SerializableData()
            .add("name", SerializableDataTypes.STRING, "")
            .add("builtin", SerializableDataTypes.BOOLEAN, false)
            .add("model", SerializableDataTypes.IDENTIFIER, null)
            .add("color", SerializableDataTypes.INT, -1)
            .add("parent", SerializableDataTypes.IDENTIFIER, new Identifier(CosmeticsMod.MODID, "body"))
            .add("translation", SerializableDataType.list(SerializableDataTypes.FLOAT), Arrays.asList(0.0F, 0.0F, 0.0F))
            .add("rotation", SerializableDataType.list(SerializableDataTypes.FLOAT), Arrays.asList(0.0F, 0.0F, 0.0F))
            .add("scale", SerializableDataType.list(SerializableDataTypes.FLOAT), Arrays.asList(0.0F, 0.0F, 0.0F));

    public static final Cosmetic EMPTY = register(new Cosmetic(new Identifier(CosmeticsMod.MODID, "empty"), "empty", true, null, null, Vec3f.ZERO, Vec3f.ZERO, Vec3f.ZERO));
    public static final Cosmetic DRAGON_WINGS = register(new Cosmetic(new Identifier(CosmeticsMod.MODID, "dragon_wings"), "dragon_wings", true, null, null, Vec3f.ZERO, Vec3f.ZERO, Vec3f.ZERO));

    private static Cosmetic register(Cosmetic cosmetic) {
        return Registry.register(Registries.COSMETICS, cosmetic.getIdentifier(), cosmetic);
    }

    @Getter
    private final Identifier identifier;
    @Getter
    private final String name;
    @Getter
    private final boolean builtin;
    @Getter
    private final Identifier model;
    @Getter
    private final Identifier parent;
    @Getter
    private final Vec3f translation;
    @Getter
    private final Vec3f rotation;
    @Getter
    private final Vec3f scale;

    public Cosmetic(Identifier id, String name, boolean builtin, Identifier model, Identifier parent, Vec3f translation, Vec3f rotation, Vec3f scale) {
        this.identifier = id;
        this.name = name;
        this.builtin = builtin;
        this.model = model;
        this.parent = parent;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public static Cosmetic createFromData(Identifier id, SerializableData.Instance data) {
        return new Cosmetic(id,
                data.getString("name"),
                data.getBoolean("builtin"),
                (Identifier) data.get("model"),
                (Identifier) data.get("parent"),
                getVec3f(data, "translation"),
                getVec3f(data, "rotation"),
                getVec3f(data, "scale"));
    }

    public static Cosmetic fromJson(Identifier id, JsonObject json) {
        return createFromData(id, DATA.read(json));
    }

    private static Vec3f getVec3f(SerializableData.Instance data, String name) {
        List<Float> list = (List<Float>) data.get(name);
        float[] array = ArrayUtils.toPrimitive(list.toArray(new Float[2]));

        return new Vec3f(array[0], array[1], array[2]);
    }
}
