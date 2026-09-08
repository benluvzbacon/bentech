package com.bentech.util;

import com.bentech.api.Material;
import com.bentech.api.Materials;
import com.bentech.block.MachineType;
import com.bentech.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

/**
 * Central catalogue of machine recipes (code-defined, matching the classic
 * GregTech processing chain):
 *
 * <ul>
 *   <li>Macerator: ore -> 2 x dust (also macerates vanilla ores)</li>
 *   <li>Electric furnace: dust -> ingot</li>
 *   <li>Compressor: ingot -> plate</li>
 *   <li>Alloy smelter: two components -> a metal dust</li>
 *   <li>Wiremill: ingot -> 2 x wire</li>
 *   <li>Recycler: dust / plate -> nugget</li>
 *   <li>Centrifuge: dust -> dust (mixes/doubles a dust)</li>
 *   <li>Generator: burnable fuel -> energy</li>
 * </ul>
 *
 * The maps are built by {@link #build()} once all items/blocks are registered.
 */
public final class MachineRecipes {

    private static final Map<Item, Item> MACERATE = new HashMap<>();
    private static final Map<Item, Item> SMELT = new HashMap<>();
    private static final Map<Item, Item> COMPRESS = new HashMap<>();
    private static final Map<Item, Item> WIRE = new HashMap<>();
    private static final Map<Item, Item> RECYCLE = new HashMap<>();
    private static final Map<Item, Item> CENTRIFUGE = new HashMap<>();
    private static final Map<ItemCombination, Item> ALLOY = new HashMap<>();
    private static final Map<Item, Integer> FUEL_TICKS = new HashMap<>();

    private MachineRecipes() {
    }

    public static void build() {
        MACERATE.clear();
        SMELT.clear();
        COMPRESS.clear();
        WIRE.clear();
        RECYCLE.clear();
        CENTRIFUGE.clear();
        ALLOY.clear();
        FUEL_TICKS.clear();

        for (Material m : Materials.all()) {
            if (m.ore != null && m.dust != null) {
                MACERATE.put(m.ore, m.dust);
            }
            if (m.dust != null && m.ingot != null) {
                SMELT.put(m.dust, m.ingot);
            }
            if (m.ingot != null && m.plate != null) {
                COMPRESS.put(m.ingot, m.plate);
            }
            if (m.ingot != null && m.wire != null) {
                WIRE.put(m.ingot, m.wire);
            }
            if (m.plate != null && m.nugget != null) {
                RECYCLE.put(m.plate, m.nugget);
            }
            if (m.dust != null && m.plate != null) {
                CENTRIFUGE.put(m.dust, m.plate);
            }
        }

        // Vanilla ores macerate into our dusts (so iron/copper/gold need no custom ore).
        MACERATE.put(Items.IRON_ORE, Materials.Iron.dust);
        MACERATE.put(Items.DEEPSLATE_IRON_ORE, Materials.Iron.dust);
        MACERATE.put(Items.RAW_IRON, Materials.Iron.dust);
        MACERATE.put(Items.COPPER_ORE, Materials.Copper.dust);
        MACERATE.put(Items.DEEPSLATE_COPPER_ORE, Materials.Copper.dust);
        MACERATE.put(Items.RAW_COPPER, Materials.Copper.dust);
        MACERATE.put(Items.GOLD_ORE, Materials.Gold.dust);
        MACERATE.put(Items.DEEPSLATE_GOLD_ORE, Materials.Gold.dust);
        MACERATE.put(Items.RAW_GOLD, Materials.Gold.dust);
        MACERATE.put(Items.ANCIENT_DEBRIS, Materials.Iridium.dust);

        ALLOY.put(ItemCombination.of(Materials.Copper.dust, Materials.Tin.dust), Materials.Bronze.dust);
        ALLOY.put(ItemCombination.of(Materials.Iron.dust, Items.COAL), Materials.Steel.dust);
        ALLOY.put(ItemCombination.of(Materials.Iron.dust, Materials.Nickel.dust), Materials.Invar.dust);
        ALLOY.put(ItemCombination.of(Materials.Copper.dust, Materials.Zinc.dust), Materials.Brass.dust);
        ALLOY.put(ItemCombination.of(Materials.Iridium.dust, Materials.Nickel.dust), Materials.IridiumAlloy.dust);

        FUEL_TICKS.put(Items.COAL, 400);
        FUEL_TICKS.put(Items.CHARCOAL, 400);
        FUEL_TICKS.put(Items.COAL_BLOCK, 3600);
        FUEL_TICKS.put(Items.LAVA_BUCKET, 3600);
        FUEL_TICKS.put(ModItems.BATTERY, 1200);
    }

    /** Returns the recipe for a machine, or null if none applies. */
    public static MachineRecipeResult process(MachineType type, ItemStack in0, ItemStack in1) {
        if (in0 == null || in0.isEmpty()) {
            return null;
        }
        return switch (type) {
            case MACERATOR -> single(MACERATE.get(in0.getItem()), 2, durationTicks(type));
            case ELECTRIC_FURNACE -> single(SMELT.get(in0.getItem()), 1, durationTicks(type));
            case COMPRESSOR -> single(COMPRESS.get(in0.getItem()), 1, durationTicks(type));
            case WIREMILL -> single(WIRE.get(in0.getItem()), 2, durationTicks(type));
            case RECYCLER -> single(RECYCLE.get(in0.getItem()), 3, durationTicks(type));
            case CENTRIFUGE -> single(CENTRIFUGE.get(in0.getItem()), 1, durationTicks(type));
            case ALLOY_SMELTER -> {
                if (in1 == null || in1.isEmpty()) {
                    yield null;
                }
                Item out = ALLOY.get(ItemCombination.of(in0.getItem(), in1.getItem()));
                yield out == null ? null : new MachineRecipeResult(new ItemStack(out, 2), durationTicks(type), true);
            }
            default -> null;
        };
    }

    private static MachineRecipeResult single(Item out, int count, int ticks) {
        return out == null ? null : new MachineRecipeResult(new ItemStack(out, count), ticks, false);
    }

    public static int durationTicks(MachineType type) {
        return switch (type) {
            case ALLOY_SMELTER -> 300;
            case CENTRIFUGE -> 260;
            case RECYCLER -> 160;
            default -> 200;
        };
    }

    public static int energyPerTick(MachineType type) {
        return switch (type) {
            case ALLOY_SMELTER -> 2;
            case CENTRIFUGE -> 3;
            default -> 1;
        };
    }

    public static long maxEnergy(MachineType type) {
        return switch (type) {
            case ALLOY_SMELTER -> 4096;
            case CENTRIFUGE -> 8192;
            default -> 2048;
        };
    }

    /** Generator energy output (EU per tick while burning). */
    public static int generatorOutput() {
        return 32;
    }

    public static int fuelTicks(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        return FUEL_TICKS.getOrDefault(stack.getItem(), 0);
    }

    public static boolean isFuel(ItemStack stack) {
        return fuelTicks(stack) > 0;
    }

    public record ItemCombination(Item a, Item b) {
        public static ItemCombination of(Item x, Item y) {
            // order independent so (copper, tin) == (tin, copper)
            ResourceLocation rx = BuiltInRegistries.ITEM.getKey(x);
            ResourceLocation ry = BuiltInRegistries.ITEM.getKey(y);
            String sx = rx == null ? String.valueOf(System.identityHashCode(x)) : rx.toString();
            String sy = ry == null ? String.valueOf(System.identityHashCode(y)) : ry.toString();
            return sx.compareTo(sy) <= 0 ? new ItemCombination(x, y) : new ItemCombination(y, x);
        }
    }

}
