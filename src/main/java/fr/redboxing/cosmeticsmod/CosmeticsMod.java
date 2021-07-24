package fr.redboxing.cosmeticsmod;

import fr.redboxing.cosmeticsmod.cosmetics.PlayerHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class CosmeticsMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("reloadcosmetics").executes(c -> {
            ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
            if(handler != null) {
                PlayerHandler.clearUsers();
                handler.getPlayerList().forEach(playerListEntry -> {
                    if(playerListEntry != null && playerListEntry.getProfile() != null) {
                        PlayerHandler.loadPlayerData(playerListEntry.getProfile().getId());
                    }
                });

                MinecraftClient.getInstance().player.sendMessage(new LiteralText(Formatting.GREEN + "Reloaded all players cosmetics !"), false);
            }
            return 1;
        }));
    }
}
