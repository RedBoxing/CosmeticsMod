package fr.redboxing.cosmeticsmod.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resource.ZipResourcePack;

import java.io.IOException;

@RequiredArgsConstructor
public class CosmeticPack {
    @Getter
    private final int id;
    @Getter
    private final String hash;
}
