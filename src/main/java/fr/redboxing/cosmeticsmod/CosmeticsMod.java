package fr.redboxing.cosmeticsmod;

import fr.redboxing.cosmeticsmod.api.API;
import fr.redboxing.cosmeticsmod.api.responses.LoginResponse;
import fr.redboxing.cosmeticsmod.data.CosmeticManager;
import fr.redboxing.cosmeticsmod.mixin.IMinecraftClient;
import fr.redboxing.cosmeticsmod.pack.CosmeticsPackProvider;
import fr.redboxing.cosmeticsmod.screen.CosmeticLoginScreen;
import fr.redboxing.cosmeticsmod.screen.CosmeticMainScreen;
import fr.redboxing.cosmeticsmod.utils.Multithreading;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CosmeticsMod implements ClientModInitializer {
    public static String MODID = "cosmeticsmod";
    public static final Logger LOGGER = LogManager.getLogger(CosmeticsMod.class);

    private static KeyBinding keyBinding;

    @Getter
    private static final API api = new API();

    @Setter
    private static Screen displayNextTick;

    @Override
    public void onInitializeClient() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.cosmeticsmod.opengui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, "category.cosmeticsmod"));
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new CosmeticManager());

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(displayNextTick != null) {
                client.setScreen(displayNextTick);
                displayNextTick = null;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(keyBinding.wasPressed()) {
                setDisplayNextTick(new CosmeticMainScreen());
            }
        });
    }
    public static void reloadCosmeticsPacks() {
        ResourceReloadLogger resourceReloadLogger = ((IMinecraftClient) MinecraftClient.getInstance()).getResourceReloadLogger();

        CosmeticsPackProvider.COSMETICS_PACK_PROVIDER.register((profileAdder) -> {
            resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.MANUAL, Collections.singletonList(profileAdder.createResourcePack()));
            resourceReloadLogger.finish();
        });
    }
}
