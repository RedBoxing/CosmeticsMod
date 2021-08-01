package fr.redboxing.cosmeticsmod.user;

import com.google.gson.JsonObject;
import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.api.responses.UserResponse;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class User {
    @Getter
    private final UUID uuid;

    @Getter
    private List<CosmeticPack> cosmeticsPacks;

    @Getter
    private Map<Identifier, JsonObject> cosmetics;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.cosmeticsPacks = new ArrayList<>();
        this.cosmetics = new HashMap<>();

        reloadUserCosmeticsPacks();
    }

    public void reloadUserCosmeticsPacks() {
        Thread t = new Thread(() -> {
            this.loadPlayerData();
            this.downloadUserCosmetics();
            CosmeticsMod.reloadCosmeticsPacks();
        });

        t.setDaemon(true);
        t.start();
    }

    public void downloadUserCosmetics() {
        new File("cosmetics").mkdir();
        this.cosmeticsPacks.forEach(pack -> {
            try {
                CosmeticsMod.getApi().downloadPack(pack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadPlayerData() {
        try {
            UserResponse userResponse = CosmeticsMod.getApi().getUser(this.uuid);
            this.cosmeticsPacks = userResponse.cosmetics_packs;
            this.cosmetics = userResponse.cosmetics.stream().collect(Collectors.toMap(cosmetic -> new Identifier(cosmetic.id), cosmetic -> cosmetic.data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
