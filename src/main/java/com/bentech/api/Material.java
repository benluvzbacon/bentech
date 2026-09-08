package com.bentech.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * A single metallic / elemental material, the heart of GregTech-style play.
 * Every material can be produced in several "forms" (dust, ingot, plate,
 * rod, gear, ...) and some appear as raw ore and as a metal block.
 */
public class Material {
    private final String name;         // registry-safe id, e.g. "iron"
    private final String displayName;  // human readable, e.g. "Iron"
    private final String symbol;       // chemical symbol, e.g. "Fe"
    private final int color;           // 0xRRGGBB used for textures and tooltips
    private final Tier tier;           // tier of machinery required to process it
    private final boolean hasOre;      // generates in the world
    private final boolean hasAlloy;    // no natural ore, produced via alloys
    private final boolean meltable;    // has a molten fluid variant

    // Registered content, filled in during mod initialization.
    public Item dust;
    public Item ingot;
    public Item nugget;
    public Item plate;
    public Item rod;
    public Item gear;
    public Item ore;
    public Item blockItem;
    public Block block;
    public Block oreBlock;

    public Material(String name, String displayName, String symbol, int color,
                    Tier tier, boolean hasOre, boolean hasAlloy, boolean meltable) {
        this.name = name;
        this.displayName = displayName;
        this.symbol = symbol;
        this.color = color;
        this.tier = tier;
        this.hasOre = hasOre;
        this.hasAlloy = hasAlloy;
        this.meltable = meltable;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getColor() {
        return color;
    }

    public Tier getTier() {
        return tier;
    }

    public boolean hasOre() {
        return hasOre;
    }

    public boolean hasAlloy() {
        return hasAlloy;
    }

    public boolean isMeltable() {
        return meltable;
    }

    @Override
    public String toString() {
        return "Material[" + name + "]";
    }
}
