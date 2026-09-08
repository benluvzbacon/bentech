package com.bentech.world;

import com.bentech.BenTech;
import com.bentech.api.Material;
import com.bentech.api.Materials;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Attaches the ore {@link PlacedFeature}s (defined as vanilla datapack JSONs in
 * {@code data/bentech/worldgen}) to the overworld. The actual feature JSONs are
 * data-driven, so only the biome hook lives in Java.
 */
public final class ModWorldgen {

    private ModWorldgen() {
    }

    public static void register() {
        GenerationStep.Decoration step = GenerationStep.Decoration.UNDERGROUND_ORES;
        for (Material m : Materials.withOre()) {
            if (m.ore == null) {
                continue;
            }
            String name = "ore_" + m.getName();
            ResourceKey<PlacedFeature> key = ResourceKey.create(Registries.PLACED_FEATURE, BenTech.id(name));
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), step, key);
        }
    }
}
