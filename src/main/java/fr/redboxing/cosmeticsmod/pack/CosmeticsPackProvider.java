package fr.redboxing.cosmeticsmod.pack;

import fr.redboxing.cosmeticsmod.CosmeticsMod;
import fr.redboxing.cosmeticsmod.api.API;
import fr.redboxing.cosmeticsmod.user.CosmeticPack;
import fr.redboxing.cosmeticsmod.user.UserManager;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.resource.*;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.resource.ResourcePackSource.PACK_SOURCE_BUILTIN;

public class CosmeticsPackProvider implements ResourcePackProvider {
    public static final ResourcePackSource RESOURCE_PACK_SOURCE = PACK_SOURCE_BUILTIN; //text -> new TranslatableText("pack.nameAndSource", text, new LiteralText("test"));
    public static final CosmeticsPackProvider COSMETICS_PACK_PROVIDER = new CosmeticsPackProvider();

    private final ResourcePackProfile.Factory factory;

    public CosmeticsPackProvider() {
        this.factory = (name, text, bl, supplier, metadata, initialPosition, source) ->
                new ResourcePackProfile(name, text, bl, supplier, metadata, ResourceType.CLIENT_RESOURCES, initialPosition, source);
    }

    public void register(Consumer<ResourcePackProfile> profileAdder) {
        register(profileAdder, this.factory);
    }


    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        CosmeticsMod.LOGGER.info("register");

        List<ResourcePack> packs = new ArrayList<>();
        appendResourcePacks(packs);

        if(!packs.isEmpty()) {
            ResourcePackProfile resourcePackProfile = ResourcePackProfile.of("Cosmetics Packs",
                    true, () -> new GroupResourcePack("Cosmetics Packs", ResourceType.CLIENT_RESOURCES, packs), factory, ResourcePackProfile.InsertionPosition.BOTTOM,
                    RESOURCE_PACK_SOURCE);

            if (resourcePackProfile != null) {
                profileAdder.accept(resourcePackProfile);
            }
        }
    }

    private void appendResourcePacks(List<ResourcePack> packs) {
        for(CosmeticPack cosmeticPack : UserManager.getAllCosmeticsPacks()) {
            for(File file : FileUtils.listFiles(new File("cosmetics"), TrueFileFilter.TRUE, null)) {
                if(!file.getName().equals(DigestUtils.sha1Hex(API.DOWNLOAD_PACK_URL + cosmeticPack.getId()))) return;

                ZipResourcePack zipResourcePack = new ZipResourcePack(file);
                packs.add(zipResourcePack);
            }
        }
    }
}
