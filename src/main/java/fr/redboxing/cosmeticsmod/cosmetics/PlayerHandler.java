package fr.redboxing.cosmeticsmod.cosmetics;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.redboxing.cosmeticsmod.cosmetics.settings.AbstractCosmeticSettings;
import fr.redboxing.cosmeticsmod.utils.CosmeticMapTypeAdapter;
import fr.redboxing.cosmeticsmod.utils.EnumCapePriority;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHandler {
    private static Map<UUID, PlayerHandler> instances = new HashMap<>();

    private UUID playerUUID;
    private Map<EnumCosmetics, AbstractCosmeticSettings> cosmetics;
    private Int2ObjectMap<NativeImage> animatedCape;

    private boolean hasAnimatedCape;

    private long lastFrameTime;
    private int lastFrame;
    private int capeInterval;

    private EnumCapePriority currentCape;

    public PlayerHandler(UUID uuid) {
        this.playerUUID = uuid;
        this.cosmetics = new HashMap<>();

        this.hasAnimatedCape = false;

        this.lastFrameTime = 0L;
        this.lastFrame = 0;
        this.capeInterval = 100;

        this.currentCape = EnumCapePriority.MOJANG;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    private NativeImage readTexture(String textureBase64) {
        try {
            byte[] imgBytes = Base64.decodeBase64(textureBase64);
            ByteArrayInputStream bias = new ByteArrayInputStream(imgBytes);
            return NativeImage.read(bias);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void applyCape(String cape, EnumCapePriority priority) {
        this.currentCape = priority;
        NativeImage capeImage = this.readTexture(cape);
        if (capeImage.getHeight() != capeImage.getWidth() / 2) {
            Int2ObjectOpenHashMap<NativeImage> animatedCape = new Int2ObjectOpenHashMap<NativeImage>();
            int totalFrames = capeImage.getHeight() / capeImage.getWidth() / 2;
            for (int currentFrame = 0; currentFrame < totalFrames; currentFrame++) {
                NativeImage frame = new NativeImage(capeImage.getWidth(), capeImage.getWidth() / 2, true);
                for (int x = 0; x < frame.getWidth(); x++) {
                    for (int y = 0; y < frame.getHeight(); y++)
                        frame.setPixelColor(x, y, capeImage.getPixelColor(x, y + currentFrame * capeImage.getWidth() / 2));
                }
                animatedCape.put(currentFrame, frame);
            }

            this.setAnimatedCape(animatedCape);
        }
        else {
            int imageWidth = 64;
            int imageHeight = 32;
            for (int srcWidth = capeImage.getWidth(), srcHeight = capeImage.getHeight(); imageWidth < srcWidth || imageHeight < srcHeight; ) {
                imageWidth *= 2;
                imageHeight *= 2;
            }
            NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
            for (int x = 0; x < capeImage.getWidth(); x++) {
                for (int y = 0; y < capeImage.getHeight(); y++)
                    imgNew.setPixelColor(x, y, capeImage.getPixelColor(x, y));
            }

            capeImage.close();
            this.applyTexture(new Identifier("cosmeticsmod", "capes/" + this.playerUUID), imgNew);
        }
    }

    public void setAnimatedCape(Int2ObjectMap<NativeImage> animatedCape) {
        this.animatedCape = animatedCape;
        this.hasAnimatedCape = true;
        this.loadFramesToResource();
    }

    private void loadFramesToResource() {
        animatedCape.forEach(((integer, nativeImage) -> {
            Identifier currentResource = new Identifier("cosmeticsmod", String.format("capes/%s/%d", this.playerUUID, integer));
            this.applyTexture(currentResource, nativeImage);
        }));
    }

    private Identifier getFrame() {
        long time = System.currentTimeMillis();
        if (time > this.lastFrameTime + this.capeInterval) {
            final int currentFrameNo = (this.lastFrame + 1 > this.animatedCape.size() - 1) ? 0 : (this.lastFrame + 1);
            this.lastFrame = currentFrameNo;
            this.lastFrameTime = time;
            return new Identifier("cosmeticsmod", String.format("capes/%s/%d", this.playerUUID, currentFrameNo));
        }
        return new Identifier("cosmeticsmod", String.format("capes/%s/%d", this.playerUUID, this.lastFrame));
    }

    public Identifier getCapeLocation() {
        if (!this.hasAnimatedCape) {
            return new Identifier("cosmeticsmod", "capes/" + this.playerUUID);
        }else {
            return this.getFrame();
        }
    }

    private void applyTexture(Identifier id, NativeImage nativeImage) {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(nativeImage));
        });
    }

    public boolean canRenderMojangCape(AbstractClientPlayerEntity player) {
        final ClientPlayNetworkHandler netHandlerPlayClient = MinecraftClient.getInstance().getNetworkHandler();
        final PlayerListEntry networkPlayerInfo = (netHandlerPlayClient != null) ? netHandlerPlayClient.getPlayerListEntry(player.getUuid()) : null;
        final Identifier locationOptifine = player.getCapeTexture();
        final Identifier locationMinecon = (networkPlayerInfo == null) ? null : networkPlayerInfo.getCapeTexture();
        final Identifier locationStellar = getCapeLocation();
        //user.setMojangCapeModified(locationMinecon == null || !locationMinecon.equals(locationOptifine));
        if (locationStellar == null) {
            //user.getCloakContainer().validateTexture(this);
        }
        boolean canRenderMojangCape = locationStellar == null;
        if ((locationMinecon != null || locationOptifine != null)) {
            canRenderMojangCape = true;
        }
        return canRenderMojangCape;
    }

    public void clear() {
        this.cosmetics.clear();
        clearCape();
    }

    public void clearCape() {
        MinecraftClient.getInstance().getTextureManager().destroyTexture(getCapeLocation());

        this.animatedCape.clear();

        this.hasAnimatedCape = false;

        this.lastFrameTime = 0L;
        this.lastFrame = 0;
        this.capeInterval = 100;
    }

    public static void onPlayerJoin(PlayerEntity player) {
        loadPlayerData(player.getUuid());
    }

    public static void loadPlayerData(UUID uuid) {
        PlayerHandler playerHandler = PlayerHandler.get(uuid);
        Thread playerDownload = new Thread(() -> {
            try {
                URL url = new URL("https://api.redboxing.fr/api/user/" + playerHandler.getPlayerUUID().toString());
                HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpurlconnection.setDoInput(true);
                httpurlconnection.setDoOutput(false);
                httpurlconnection.connect();
                if (httpurlconnection.getResponseCode() / 100 == 2) {
                    Reader reader = new InputStreamReader(httpurlconnection.getInputStream(), StandardCharsets.UTF_8);
                    ProfileResult profileResult = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<EnumCosmetics, AbstractCosmeticSettings>>(){}.getType(), new CosmeticMapTypeAdapter()).create().fromJson(reader, ProfileResult.class);
                    playerHandler.setCosmetics(profileResult.cosmetics_settings);

                    if(playerHandler.hasCosmetic(EnumCosmetics.CAPE) && playerHandler.getCosmetic(EnumCosmetics.CAPE).isEnabled() && playerHandler.getCosmetic(EnumCosmetics.CAPE).getTexture() != null) {
                        playerHandler.applyCape(playerHandler.getCosmetic(EnumCosmetics.CAPE).getTexture(), EnumCapePriority.COSMETICSMOD);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        playerDownload.setDaemon(true);
        playerDownload.start();
    }

    public void savePlayerData() {
        Thread playerDownload = new Thread(() -> {
            try {
                URL url = new URL("https://api.redboxing.fr/api/user/" + getPlayerUUID().toString());
                HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpurlconnection.setRequestMethod("POST");
                httpurlconnection.setDoOutput(false);

                ProfileResult profileResult = new ProfileResult();
                profileResult.cosmetics_settings = getCosmetics();

                byte[] out = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<EnumCosmetics, AbstractCosmeticSettings>>(){}.getType(), new CosmeticMapTypeAdapter()).create().toJson(profileResult, ProfileResult.class).getBytes(StandardCharsets.UTF_8);
                int length = out.length;

                httpurlconnection.setFixedLengthStreamingMode(length);
                httpurlconnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpurlconnection.connect();
                try(OutputStream os = httpurlconnection.getOutputStream()) {
                    os.write(out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        playerDownload.setDaemon(true);
        playerDownload.start();
    }

    public static void clearUsers() {
        instances.clear();
    }

    public static PlayerHandler get(PlayerEntity player) {
        return instances.computeIfAbsent(player.getUuid(), PlayerHandler::new);
    }

    public static PlayerHandler get(UUID uuid) {
        return instances.computeIfAbsent(uuid, PlayerHandler::new);
    }

    public boolean hasCosmetic(EnumCosmetics cosmeticType) {
        return this.cosmetics.containsKey(cosmeticType);
    }

    public AbstractCosmeticSettings getCosmetic(EnumCosmetics cosmetic) {
        return this.cosmetics.get(cosmetic);
    }

    public Map<EnumCosmetics, AbstractCosmeticSettings> getCosmetics() {
        return cosmetics;
    }

    public void setCosmetics(Map<EnumCosmetics, AbstractCosmeticSettings> cosmetics) {
        this.cosmetics = cosmetics;
    }

    static class ProfileResult {
       // private List<EnumCosmetics> cosmetics;
        private Map<EnumCosmetics, AbstractCosmeticSettings> cosmetics_settings = new HashMap<>();
    }
}
