package com.bentech.api;

/**
 * Voltage / technology tiers, mirroring the classic GregTech progression.
 * Each tier has an associated nominal voltage (in arbitrary energy units, "EU")
 * and the machines built at that tier require that energy to operate.
 */
public enum Tier {
    ULV("ULV", "Ultra Low Voltage", 8),
    LV("LV", "Low Voltage", 32),
    MV("MV", "Medium Voltage", 128),
    HV("HV", "High Voltage", 512),
    EV("EV", "Extreme Voltage", 2048),
    IV("IV", "Insane Voltage", 8192),
    LuV("LuV", "Ludicrous Voltage", 32768),
    ZPM("ZPM", "Zero Point Module", 131072),
    UV("UV", "Ultimate Voltage", 524288);

    private final String id;
    private final String displayName;
    private final long nominalVoltage;

    Tier(String id, String displayName, long nominalVoltage) {
        this.id = id;
        this.displayName = displayName;
        this.nominalVoltage = nominalVoltage;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Nominal voltage (energy units per operation "packet") for this tier. */
    public long getNominalVoltage() {
        return nominalVoltage;
    }
}
