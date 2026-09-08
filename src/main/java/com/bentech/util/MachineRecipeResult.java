package com.bentech.util;

import net.minecraft.world.item.ItemStack;

/**
 * Describes the outcome of a machine recipe: the output stack, how many ticks
 * the operation takes and whether it consumes two input slots.
 */
public record MachineRecipeResult(ItemStack output, int durationTicks, boolean requiresTwoInputs) {
}
