package com.bentech.block;

import com.bentech.api.Tier;

/**
 * The kinds of machines in the mod. Each entry drives block registration,
 * block entity behaviour and the processing recipe set used by that machine.
 */
public enum MachineType {
    MACERATOR("macerator", "Macerator", Tier.LV, MachineKind.PROCESSING),
    ELECTRIC_FURNACE("electric_furnace", "Electric Furnace", Tier.LV, MachineKind.PROCESSING),
    ALLOY_SMELTER("alloy_smelter", "Alloy Smelter", Tier.LV, MachineKind.PROCESSING),
    COMPRESSOR("compressor", "Compressor", Tier.LV, MachineKind.PROCESSING),
    WIREMILL("wiremill", "Wiremill", Tier.LV, MachineKind.PROCESSING),
    RECYCLER("recycler", "Recycler", Tier.LV, MachineKind.PROCESSING),
    CENTRIFUGE("centrifuge", "Centrifuge", Tier.LV, MachineKind.PROCESSING),
    GENERATOR("generator", "Generator", Tier.LV, MachineKind.GENERATOR);

    private final String id;
    private final String displayName;
    private final Tier tier;
    private final MachineKind kind;

    MachineType(String id, String displayName, Tier tier, MachineKind kind) {
        this.id = id;
        this.displayName = displayName;
        this.tier = tier;
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Tier getTier() {
        return tier;
    }

    public MachineKind getKind() {
        return kind;
    }

    public boolean hasSecondInput() {
        return this == ALLOY_SMELTER;
    }

    public enum MachineKind {
        PROCESSING,
        GENERATOR
    }
}
