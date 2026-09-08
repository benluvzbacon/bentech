package com.bentech.registry;

import com.bentech.BenTech;
import com.bentech.block.entity.GeneratorBlockEntity;
import com.bentech.block.entity.ProcessingMachineBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Registers the two block entity types used by all machines.
 */
public final class ModBlockEntities {

    public static final BlockEntityType<ProcessingMachineBlockEntity> PROCESSING = register(
            "processing_machine",
            BlockEntityType.Builder.of(
                    ProcessingMachineBlockEntity::new,
                    ModBlocks.MACERATOR,
                    ModBlocks.ELECTRIC_FURNACE,
                    ModBlocks.ALLOY_SMELTER,
                    ModBlocks.COMPRESSOR).build());

    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = register(
            "generator",
            BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.GENERATOR).build());

    private ModBlockEntities() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, BlockEntityType<T> type) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BenTech.MOD_ID, path);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type);
    }
}
