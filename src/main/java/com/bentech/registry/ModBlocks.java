package com.bentech.registry;

import com.bentech.BenTech;
import com.bentech.api.Material;
import com.bentech.api.Materials;
import com.bentech.block.CableBlock;
import com.bentech.block.CreativeEnergyBlock;
import com.bentech.block.MachineBlock;
import com.bentech.block.MachineType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Registers machine blocks, cable blocks, metal blocks and ore blocks.
 */
public final class ModBlocks {

    public static final MachineBlock MACERATOR = machine(MachineType.MACERATOR);
    public static final MachineBlock ELECTRIC_FURNACE = machine(MachineType.ELECTRIC_FURNACE);
    public static final MachineBlock ALLOY_SMELTER = machine(MachineType.ALLOY_SMELTER);
    public static final MachineBlock COMPRESSOR = machine(MachineType.COMPRESSOR);
    public static final MachineBlock WIREMILL = machine(MachineType.WIREMILL);
    public static final MachineBlock RECYCLER = machine(MachineType.RECYCLER);
    public static final MachineBlock CENTRIFUGE = machine(MachineType.CENTRIFUGE);
    public static final MachineBlock GENERATOR = machine(MachineType.GENERATOR);

    public static final Block CABLE = cable();

    public static final CreativeEnergyBlock CREATIVE_ENERGY = creativeEnergy();

    private ModBlocks() {
    }

    public static void load() {
        for (Material m : Materials.all()) {
            if (m.block == null) {
                Block metal = new Block(metalProperties());
                m.blockItem = registerBlockItem(metal, "block_" + m.getName());
                m.block = metal;
            }
            if (m.hasOre() && m.oreBlock == null) {
                Block ore = new Block(oreProperties());
                m.ore = registerBlockItem(ore, "ore_" + m.getName());
                m.oreBlock = ore;
            }
        }
    }

    // ---------------------------------------------------------------- helpers

    private static MachineBlock machine(MachineType type) {
        MachineBlock block = new MachineBlock(machineProperties(), type);
        registerBlock(block, type.getId());
        return block;
    }

    private static Block cable() {
        CableBlock block = new CableBlock(machineProperties());
        registerBlock(block, "cable");
        return block;
    }

    private static CreativeEnergyBlock creativeEnergy() {
        CreativeEnergyBlock block = new CreativeEnergyBlock(machineProperties());
        registerBlock(block, "creative_energy");
        return block;
    }

    private static void registerBlock(Block block, String path) {
        ResourceLocation id = id(path);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
    }

    private static BlockItem registerBlockItem(Block block, String path) {
        ResourceLocation id = id(path);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        BlockItem item = new BlockItem(block, new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return item;
    }

    private static BlockBehaviour.Properties machineProperties() {
        return BlockBehaviour.Properties.of().strength(2.0f, 6.0f).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties metalProperties() {
        return BlockBehaviour.Properties.of().strength(4.0f, 6.0f).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties oreProperties() {
        return BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops();
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(BenTech.MOD_ID, path);
    }
}
