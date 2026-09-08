package com.bentech.block.entity;

import com.bentech.block.MachineBlock;
import com.bentech.block.MachineType;
import com.bentech.registry.ModBlockEntities;
import com.bentech.util.MachineRecipes;
import com.bentech.util.MachineRecipeResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A machine that converts input item(s) into an output item over time while
 * consuming energy: macerator (ore -> dust), electric furnace (dust -> ingot),
 * compressor (ingot -> plate) and alloy smelter (two inputs -> alloy).
 */
public class ProcessingMachineBlockEntity extends AbstractMachineBlockEntity {

    private final ItemStack[] items = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    private final MachineType machineType;
    public int progress;

    public ProcessingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROCESSING, pos, state);
        this.machineType = state.getBlock() instanceof MachineBlock block ? block.getType() : MachineType.MACERATOR;
    }

    public MachineType getMachineType() {
        return machineType;
    }

    @Override
    protected ItemStack getSlotItem(int slot) {
        return slot >= 0 && slot < items.length ? items[slot] : ItemStack.EMPTY;
    }

    @Override
    protected void setSlotItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < items.length) {
            items[slot] = stack;
        }
    }

    @Override
    public long getMaxEnergy() {
        return MachineRecipes.maxEnergy(machineType);
    }

    @Override
    public boolean isActive() {
        return progress > 0;
    }

    @Override
    public void tickServer(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }
        int ept = MachineRecipes.energyPerTick(machineType);
        ItemStack in0 = getItem(SLOT_INPUT_0);
        ItemStack in1 = getItem(SLOT_INPUT_1);
        ItemStack out = getItem(SLOT_OUTPUT);

        MachineRecipeResult result = MachineRecipes.process(machineType, in0, in1);
        if (result == null || !canAccept(out, result.output())) {
            progress = 0;
            return;
        }
        if (!hasEnergy(ept)) {
            return;
        }
        consumeEnergy(ept);
        progress++;
        if (progress >= result.durationTicks()) {
            if (result.requiresTwoInputs()) {
                consumeOne(SLOT_INPUT_0);
                consumeOne(SLOT_INPUT_1);
            } else {
                consumeOne(SLOT_INPUT_0);
            }
            ItemStack output = result.output();
            ItemStack current = getItem(SLOT_OUTPUT);
            if (current.isEmpty()) {
                setItem(SLOT_OUTPUT, output.copy());
            } else {
                current.grow(output.getCount());
            }
            progress = 0;
            setChanged();
        }
    }

    private void consumeOne(int slot) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                setSlotItem(slot, ItemStack.EMPTY);
            }
            setChanged();
        }
    }

    private boolean canAccept(ItemStack current, ItemStack output) {
        if (current.isEmpty()) {
            return true;
        }
        if (!current.is(output.getItem())) {
            return false;
        }
        return current.getCount() + output.getCount() <= current.getMaxStackSize();
    }
}
