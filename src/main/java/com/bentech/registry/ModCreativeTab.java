package com.bentech.registry;

import com.bentech.BenTech;
import com.bentech.api.Material;
import com.bentech.api.Materials;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The BenTech creative tab. Registered as a {@code CreativeModeTab} under a
 * {@link ResourceKey}, populated via {@code displayItems}. The tab icon is
 * guaranteed non-empty (falls back to a vanilla item) so the tab always renders.
 */
public final class ModCreativeTab {

    public static final ResourceKey<CreativeModeTab> KEY =
            ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), BenTech.id("main"));

    private ModCreativeTab() {
    }

    public static void load() {
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.bentech"))
                .icon(ModCreativeTab::icon)
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
                    output.accept(new ItemStack(ModBlocks.CREATIVE_ENERGY));
                    output.accept(new ItemStack(ModItems.MACHINE_CASING));
                    output.accept(new ItemStack(ModItems.BASIC_CIRCUIT));
                    output.accept(new ItemStack(ModItems.ADVANCED_CIRCUIT));
                    output.accept(new ItemStack(ModItems.ELECTRIC_MOTOR));
                    output.accept(new ItemStack(ModItems.ELECTRIC_PUMP));
                    output.accept(new ItemStack(ModItems.CAPACITOR));
                    output.accept(new ItemStack(ModItems.BATTERY));
                    output.accept(new ItemStack(ModItems.EMITTER));
                    output.accept(new ItemStack(ModItems.SENSOR));
                    output.accept(new ItemStack(ModItems.BASIC_GEARBOX));
                    output.accept(new ItemStack(ModItems.ADVANCED_GEARBOX));
                    output.accept(new ItemStack(ModItems.PISTON));
                    output.accept(new ItemStack(ModItems.CONVEYOR));
                    output.accept(new ItemStack(ModItems.ROBOT_ARM));
                    output.accept(new ItemStack(ModItems.MAGNET));
                    output.accept(new ItemStack(ModItems.COIL));
                    output.accept(new ItemStack(ModItems.FIELD_GENERATOR));
                    output.accept(new ItemStack(ModItems.WRENCH));
                    output.accept(new ItemStack(ModItems.HAMMER));
                    output.accept(new ItemStack(ModItems.SCREWDRIVER));
                    output.accept(new ItemStack(ModItems.WIRE_CUTTER));
                    output.accept(new ItemStack(ModItems.FILE));
                    output.accept(new ItemStack(ModItems.CROWBAR));
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
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, KEY, tab);
    }

    /** A tab icon that is never empty (falls back to a vanilla item). */
    private static ItemStack icon() {
        Item icon = Materials.Iron.dust != null ? Materials.Iron.dust
                : (Materials.Iron.ingot != null ? Materials.Iron.ingot : Items.IRON_INGOT);
        return new ItemStack(icon);
    }

    private static void accept(CreativeModeTab.Output output, Item item) {
        if (item != null) {
            output.accept(new ItemStack(item));
        }
    }
}
