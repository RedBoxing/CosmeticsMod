package fr.redboxing.cosmeticsmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class,
            (listeners) -> (player) -> {
                for (PlayerJoinCallback listener : listeners) {
                    listener.call(player);
                }
            });

    void call(PlayerEntity player);
}
