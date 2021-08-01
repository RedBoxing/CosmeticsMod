package fr.redboxing.cosmeticsmod.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.Registries;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;

public class CosmeticManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public CosmeticManager() {
        super(GSON, "cosmetics");
    }

    @SneakyThrows
    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        prepared.forEach((id, json) -> {
            try {
                Cosmetic cosmetic = Cosmetic.fromJson(id, json.getAsJsonObject());
                if(!Registries.COSMETICS.containsId(id)) {
                    Registry.register(Registries.COSMETICS, id, cosmetic);
                }
            } catch(Exception e) {
                CosmeticsMod.LOGGER.error("There was a problem reading Cosmetic file " + id.toString() + " (skipping): " + e.getMessage());
            }
        });
        CosmeticsMod.LOGGER.info("Finished loading cosmetics from data files. Registry contains " + Registries.COSMETICS.getIds().size() + " cosmetics.");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(CosmeticsMod.MODID, "cosmetics");
    }
}
