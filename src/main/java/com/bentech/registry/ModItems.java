package com.bentech.registry;

import com.bentech.BenTech;
import com.bentech.api.Material;
import com.bentech.api.Materials;
import com.bentech.item.MaterialItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Function;

/**
 * Registers every material-form item and the machine component items.
 * Forms: dust, ingot, nugget, plate, rod, gear. The corresponding ore / block
 * items are registered by {@link ModBlocks} so that placement works.
 */
public final class ModItems {

    public static final Item MACHINE_CASING = register("machine_casing", new Item(plain(64)));
    public static final Item BASIC_CIRCUIT = register("basic_circuit", new Item(plain(64)));
    public static final Item ADVANCED_CIRCUIT = register("advanced_circuit", new Item(plain(64)));
    public static final Item ELECTRIC_MOTOR = register("electric_motor", new Item(plain(64)));
    public static final Item ELECTRIC_PUMP = register("electric_pump", new Item(plain(64)));
    public static final Item CAPACITOR = register("capacitor", new Item(plain(64)));

    private ModItems() {
    }

    private static Item.Properties plain(int maxStack) {
        return new Item.Properties().stacksTo(maxStack);
    }

    private static <T extends Item> T register(String path, T item) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BenTech.MOD_ID, path);
        Registry.register(Registries.ITEM, id, item);
        return item;
    }

    public static void load() {
        // Material forms
        for (Material m : Materials.all()) {
            m.dust = register(m.getName() + "_dust", new MaterialItem(m, "dust", plain(64)));
            m.ingot = register(m.getName() + "_ingot", new MaterialItem(m, "ingot", plain(64)));
            m.nugget = register(m.getName() + "_nugget", new MaterialItem(m, "nugget", plain(64)));
            m.plate = register(m.getName() + "_plate", new MaterialItem(m, "plate", plain(64)));
            m.rod = register(m.getName() + "_rod", new MaterialItem(m, "rod", plain(64)));
            m.gear = register(m.getName() + "_gear", new MaterialItem(m, "gear", plain(64)));
        }
    }

    /** Helper to retrieve a material form item if it exists. */
    public static Item form(Material m, String form) {
        Function<Material, Item> getter;
        switch (form) {
            case "dust" -> getter = mat -> mat.dust;
            case "ingot" -> getter = mat -> mat.ingot;
            case "nugget" -> getter = mat -> mat.nugget;
            case "plate" -> getter = mat -> mat.plate;
            case "rod" -> getter = mat -> mat.rod;
            case "gear" -> getter = mat -> mat.gear;
            default -> getter = mat -> mat.ingot;
        }
        return getter.apply(m);
    }
}
