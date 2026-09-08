package com.bentech.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordered catalog of all materials in the mod. The order here defines the
 * progression: the player starts with low voltage metals and works up toward
 * exotic, high-voltage materials.
 */
public final class Materials {

    public static final Material Bronze =
            new Material("bronze", "Bronze", "(Cu+Sn)", 0xBE7448, Tier.LV, false, true, true);
    public static final Material Iron =
            new Material("iron", "Iron", "Fe", 0xC8C8C8, Tier.LV, true, false, true);
    public static final Material Copper =
            new Material("copper", "Copper", "Cu", 0xB87333, Tier.LV, true, false, true);
    public static final Material Tin =
            new Material("tin", "Tin", "Sn", 0xD0D0D0, Tier.LV, true, false, true);
    public static final Material Zinc =
            new Material("zinc", "Zinc", "Zn", 0xA0A0A0, Tier.LV, true, false, true);
    public static final Material Lead =
            new Material("lead", "Lead", "Pb", 0x66666F, Tier.LV, true, false, true);
    public static final Material Silver =
            new Material("silver", "Silver", "Ag", 0xC0C0C0, Tier.LV, true, false, true);
    public static final Material Gold =
            new Material("gold", "Gold", "Au", 0xF2D230, Tier.LV, true, false, true);

    public static final Material Steel =
            new Material("steel", "Steel", "Fe(C)", 0x8F8F8F, Tier.MV, false, true, true);
    public static final Material Nickel =
            new Material("nickel", "Nickel", "Ni", 0xD5D5D5, Tier.MV, true, false, true);
    public static final Material Invar =
            new Material("invar", "Invar", "FeNi", 0xB9C1B4, Tier.MV, false, true, true);
    public static final Material Aluminium =
            new Material("aluminium", "Aluminium", "Al", 0xD6D6D6, Tier.MV, true, false, true);
    public static final Material Brass =
            new Material("brass", "Brass", "CuZn", 0xE1BE5B, Tier.MV, false, true, true);

    public static final Material Titanium =
            new Material("titanium", "Titanium", "Ti", 0x9A9A9A, Tier.HV, true, false, true);
    public static final Material Chrome =
            new Material("chrome", "Chrome", "Cr", 0xB9F3E0, Tier.HV, true, false, true);
    public static final Material Tungsten =
            new Material("tungsten", "Tungsten", "W", 0x9B9B9B, Tier.HV, true, false, true);
    public static final Material Platinum =
            new Material("platinum", "Platinum", "Pt", 0xE0E0E0, Tier.EV, true, false, true);

    public static final Material Iridium =
            new Material("iridium", "Iridium", "Ir", 0xDBDBDB, Tier.EV, true, false, true);
    public static final Material Osmium =
            new Material("osmium", "Osmium", "Os", 0xA0A0A0, Tier.IV, true, false, true);

    private static final List<Material> ALL;

    static {
        List<Material> list = new ArrayList<>();
        list.add(Bronze);
        list.add(Iron);
        list.add(Copper);
        list.add(Tin);
        list.add(Zinc);
        list.add(Lead);
        list.add(Silver);
        list.add(Gold);
        list.add(Steel);
        list.add(Nickel);
        list.add(Invar);
        list.add(Aluminium);
        list.add(Brass);
        list.add(Titanium);
        list.add(Chrome);
        list.add(Tungsten);
        list.add(Platinum);
        list.add(Iridium);
        list.add(Osmium);
        ALL = Collections.unmodifiableList(list);
    }

    private Materials() {
    }

    /** All materials, in progression order. */
    public static List<Material> all() {
        return ALL;
    }

    /** Ordered list of materials that generate as world ore. */
    public static List<Material> withOre() {
        List<Material> out = new ArrayList<>();
        for (Material m : ALL) {
            if (m.hasOre()) {
                out.add(m);
            }
        }
        return out;
    }
}
