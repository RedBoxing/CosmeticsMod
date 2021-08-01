package fr.redboxing.cosmeticsmod.api.responses;

import com.google.gson.JsonObject;
import fr.redboxing.cosmeticsmod.user.CosmeticPack;

import java.util.List;

public class UserResponse {
    public List<CosmeticPack> cosmetics_packs;
    public List<UserCosmetics> cosmetics;

    public static class UserCosmetics {
        public String id;
        public JsonObject data;
    }
}
