package fr.redboxing.cosmeticsmod.utils;

import com.google.gson.*;
import fr.redboxing.cosmeticsmod.cosmetics.EnumCosmetics;
import fr.redboxing.cosmeticsmod.cosmetics.settings.AbstractCosmeticSettings;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

public class CosmeticMapTypeAdapter implements JsonSerializer<Map<EnumCosmetics, AbstractCosmeticSettings>>, JsonDeserializer<Map<EnumCosmetics, AbstractCosmeticSettings>> {
    @Override
    public Map<EnumCosmetics, AbstractCosmeticSettings> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.getAsJsonObject().entrySet().stream().collect(Collectors.toMap(p -> EnumCosmetics.valueOf(p.getKey()), e -> new Gson().fromJson(e.getValue(), EnumCosmetics.valueOf(e.getKey()).getSettings())));
    }

    @Override
    public JsonElement serialize(Map<EnumCosmetics, AbstractCosmeticSettings> src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonParser().parse(new Gson().toJson(src));
    }
}