package fr.redboxing.cosmeticsmod.user;

import fr.redboxing.cosmeticsmod.events.PlayerJoinCallback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class UserManager {
    private static Map<UUID, User> users = new HashMap<>();
    
    public UserManager() {
        PlayerJoinCallback.EVENT.register(this::onPlayerJoin);
    }

    public static User get(PlayerEntity playerEntity) {
        return get(playerEntity.getUuid());
    }

    public static User get(UUID uuid) {
        return users.computeIfAbsent(uuid, User::new);
    }

    private void onPlayerJoin(PlayerEntity playerEntity) {
        if(playerEntity instanceof AbstractClientPlayerEntity) {
            users.computeIfAbsent(playerEntity.getUuid(), User::new);
        }
    }

    public static List<CosmeticPack> getAllCosmeticsPacks() {
        List<CosmeticPack> packs = new ArrayList<>();
        users.forEach((uuid, user) -> packs.addAll(user.getCosmeticsPacks()));
        return packs;
    }
}
