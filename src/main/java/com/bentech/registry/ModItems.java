package com.bentech.registry;

import com.bentech.BenTech;
import com.bentech.api.Material;
import com.bentech.api.Materials;
import com.bentech.api.Tier;
import com.bentech.item.ComponentItem;
import com.bentech.item.MaterialItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Function;

/**
 * Registers every material-form item and the machine component / tool items.
 * Forms: dust, ingot, nugget, plate, rod, gear, wire. The corresponding ore /
 * block items are registered by {@link ModBlocks} so that placement works.
 *
 * <p>All current components and tools are {@link Tier#LV}; higher voltage
 * tiers (MV and beyond) are intentionally left to later progression.</p>
 */
public final class ModItems {

    // Machine components (all LV).
    public static final Item MACHINE_CASING = comp(Tier.LV, "machine_casing");
    public static final Item BASIC_CIRCUIT = comp(Tier.LV, "basic_circuit");
    public static final Item ADVANCED_CIRCUIT = comp(Tier.LV, "advanced_circuit");
    public static final Item ELECTRIC_MOTOR = comp(Tier.LV, "electric_motor");
    public static final Item ELECTRIC_PUMP = comp(Tier.LV, "electric_pump");
    public static final Item CAPACITOR = comp(Tier.LV, "capacitor");
    public static final Item BATTERY = comp(Tier.LV, "battery");
    public static final Item EMITTER = comp(Tier.LV, "emitter");
    public static final Item SENSOR = comp(Tier.LV, "sensor");

    // Extra GregTech components (all LV for now).
    public static final Item BASIC_GEARBOX = comp(Tier.LV, "basic_gearbox");
    public static final Item ADVANCED_GEARBOX = comp(Tier.LV, "advanced_gearbox");
    public static final Item PISTON = comp(Tier.LV, "piston");
    public static final Item CONVEYOR = comp(Tier.LV, "conveyor");
    public static final Item ROBOT_ARM = comp(Tier.LV, "robot_arm");
    public static final Item MAGNET = comp(Tier.LV, "magnet");
    public static final Item COIL = comp(Tier.LV, "coil");
    public static final Item FIELD_GENERATOR = comp(Tier.LV, "field_generator");

    // Electromechanical hand tools (all LV).
    public static final Item WRENCH = comp(Tier.LV, "wrench");
    public static final Item HAMMER = comp(Tier.LV, "hammer");
    public static final Item SCREWDRIVER = comp(Tier.LV, "screwdriver");
    public static final Item WIRE_CUTTER = comp(Tier.LV, "wire_cutter");
    public static final Item FILE = comp(Tier.LV, "file");
    public static final Item CROWBAR = comp(Tier.LV, "crowbar");

    private ModItems() {
    }

    private static Item comp(Tier tier, String path) {
        return register(path, new ComponentItem(tier, plain(64)));
    }

    private static Item.Properties plain(int maxStack) {
        return new Item.Properties().stacksTo(maxStack);
    }

    private static <T extends Item> T register(String path, T item) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BenTech.MOD_ID, path);
        Registry.register(BuiltInRegistries.ITEM, id, item);
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
            m.wire = register(m.getName() + "_wire", new MaterialItem(m, "wire", plain(64)));
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
            case "wire" -> getter = mat -> mat.wire;
            default -> getter = mat -> mat.ingot;
        }
        return getter.apply(m);
    }
}
