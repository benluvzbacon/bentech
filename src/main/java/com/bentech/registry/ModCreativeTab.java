package com.bentech.registry;

import com.bentech.BenTech;
import com.bentech.api.Material;
import com.bentech.api.Materials;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A single creative tab containing every material form, machine and component
 * so the whole progression loop is explorable.
 */
public final class ModCreativeTab {

    private ModCreativeTab() {
    }

    public static void load() {
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.bentech"))
                .icon(() -> new ItemStack(Materials.Iron.dust == null ? Materials.Iron.ingot : Materials.Iron.dust))
                .displayItems((params, output) -> {
                    output.accept(new ItemStack(ModBlocks.MACERATOR));
                    output.accept(new ItemStack(ModBlocks.ELECTRIC_FURNACE));
                    output.accept(new ItemStack(ModBlocks.ALLOY_SMELTER));
                    output.accept(new ItemStack(ModBlocks.COMPRESSOR));
                    output.accept(new ItemStack(ModBlocks.WIREMILL));
                    output.accept(new ItemStack(ModBlocks.RECYCLER));
                    output.accept(new ItemStack(ModBlocks.CENTRIFUGE));
                    output.accept(new ItemStack(ModBlocks.GENERATOR));
                    output.accept(new ItemStack(ModBlocks.CABLE));
                    output.accept(new ItemStack(ModItems.MACHINE_CASING));
                    output.accept(new ItemStack(ModItems.BASIC_CIRCUIT));
                    output.accept(new ItemStack(ModItems.ADVANCED_CIRCUIT));
                    output.accept(new ItemStack(ModItems.ELECTRIC_MOTOR));
                    output.accept(new ItemStack(ModItems.ELECTRIC_PUMP));
                    output.accept(new ItemStack(ModItems.CAPACITOR));
                    output.accept(new ItemStack(ModItems.BATTERY));
                    output.accept(new ItemStack(ModItems.EMITTER));
                    output.accept(new ItemStack(ModItems.SENSOR));
                    for (Material m : Materials.all()) {
                        accept(output, m.dust);
                        accept(output, m.ingot);
                        accept(output, m.nugget);
                        accept(output, m.plate);
                        accept(output, m.rod);
                        accept(output, m.gear);
                        accept(output, m.wire);
                        accept(output, m.ore);
                        accept(output, m.blockItem);
                    }
                })
                .build();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BenTech.MOD_ID, "main");
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
    }

    private static void accept(CreativeModeTab.Output output, Item item) {
        if (item != null) {
            output.accept(new ItemStack(item));
        }
    }
}
