package fr.redboxing.cosmeticsmod.pack;

import com.google.common.base.Charsets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.mixin.resource.loader.NamespaceResourceManagerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GroupResourcePack implements ResourcePack {
    protected final String name;
    protected final ResourceType type;
    protected final List<ResourcePack> packs;
    protected final Map<String, List<ResourcePack>> namespacedPacks = new Object2ObjectOpenHashMap<>();

    public GroupResourcePack(String name, ResourceType type, List<ResourcePack> packs) {
        this.name = name;
        this.type = type;
        this.packs = packs;
        this.packs.forEach(pack -> pack.getNamespaces(this.type)
                .forEach(namespace -> this.namespacedPacks.computeIfAbsent(namespace, value -> new ArrayList<>())
                        .add(pack)));
    }

    @Override
    public InputStream openRoot(String fileName) throws IOException {
        if ("pack.mcmeta".equals(fileName)) {
            String description = "Mod resources.";
            String pack = String.format("{\"pack\":{\"pack_format\":" + type.getPackVersion(SharedConstants.getGameVersion()) + ",\"description\":\"%s\"}}", description);
            return IOUtils.toInputStream(pack, Charsets.UTF_8);
        } else if ("pack.png".equals(fileName)) {
            InputStream stream = FabricLoader.getInstance().getModContainer("fabric-resource-loader-v0")
                    .flatMap(container -> container.getMetadata().getIconPath(512).map(container::getPath))
                    .filter(Files::exists)
                    .map(iconPath -> {
                        try {
                            return Files.newInputStream(iconPath);
                        } catch (IOException e) {
                            return null;
                        }
                    }).orElse(null);

            if (stream != null) {
                return stream;
            }
        }

        // ReloadableResourceManagerImpl gets away with FileNotFoundException.
        throw new FileNotFoundException("\"" + fileName + "\" in Fabric mod resource pack");
    }

    @Override
    public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        try {
            InputStream inputStream = this.openRoot("pack.mcmeta");
            Throwable error = null;
            T metadata;

            try {
                metadata = AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
            } catch (Throwable e) {
                error = e;
                throw e;
            } finally {
                if (inputStream != null) {
                    if (error != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e) {
                            error.addSuppressed(e);
                        }
                    } else {
                        inputStream.close();
                    }
                }
            }

            return metadata;
        } catch (FileNotFoundException | RuntimeException e) {
            return null;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        List<ResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

        if (packs != null) {
            for (int i = packs.size() - 1; i >= 0; i--) {
                ResourcePack pack = packs.get(i);

                if (pack.contains(type, id)) {
                    return pack.open(type, id);
                }
            }
        }

        throw new ResourceNotFoundException(null,
                String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath()));
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        List<ResourcePack> packs = this.namespacedPacks.get(namespace);

        if (packs == null) {
            return Collections.emptyList();
        }

        Set<Identifier> resources = new HashSet<>();

        for (int i = packs.size() - 1; i >= 0; i--) {
            ResourcePack pack = packs.get(i);
            Collection<Identifier> modResources = pack.findResources(type, namespace, prefix, maxDepth, pathFilter);

            resources.addAll(modResources);
        }

        return resources;
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        List<ResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

        if (packs == null) {
            return false;
        }

        for (int i = packs.size() - 1; i >= 0; i--) {
            ResourcePack pack = packs.get(i);

            if (pack.contains(type, id)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return this.namespacedPacks.keySet();
    }

    public void appendResources(NamespaceResourceManagerAccessor manager, Identifier id, List<Resource> resources) throws IOException {
        List<ResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

        if (packs == null) {
            return;
        }

        Identifier metadataId = NamespaceResourceManagerAccessor.fabric$accessor_getMetadataPath(id);

        for (ResourcePack pack : packs) {
            if (pack.contains(manager.getType(), id)) {
                InputStream metadataInputStream = pack.contains(manager.getType(), metadataId) ? manager.fabric$accessor_open(metadataId, pack) : null;
                resources.add(new ResourceImpl(pack.getName(), id, manager.fabric$accessor_open(id, pack), metadataInputStream));
            }
        }
    }

    public String getFullName() {
        return this.getName() + " (" + this.packs.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public void close() {
        this.packs.forEach(ResourcePack::close);
    }
}
