package com.bentech.block.entity;

import com.bentech.block.MachineType;
import com.bentech.registry.ModBlockEntities;
import com.bentech.util.MachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A combustion generator. It burns fuel from its input slot to fill its own
 * energy buffer and pushes EU into adjacent machines — a simple energy "grid".
 */
public class GeneratorBlockEntity extends AbstractMachineBlockEntity {

    private final ItemStack[] items = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    public int burnTicksLeft;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR, pos, state);
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
        return MachineRecipes.maxEnergy(MachineType.GENERATOR);
    }

    @Override
    public boolean isActive() {
        return burnTicksLeft > 0;
    }

    @Override
    public void tickServer(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }
        int output = MachineRecipes.generatorOutput();
        if (burnTicksLeft <= 0) {
            ItemStack fuel = getItem(SLOT_INPUT_0);
            if (MachineRecipes.isFuel(fuel)) {
                burnTicksLeft = MachineRecipes.fuelTicks(fuel);
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    setSlotItem(SLOT_INPUT_0, ItemStack.EMPTY);
                }
                setChanged();
            } else {
                return;
            }
        } else {
            burnTicksLeft--;
        }

        addEnergy(output);
        distribute(level, pos, output);
    }

    private void distribute(Level level, BlockPos pos, int amount) {
        for (Direction direction : Direction.values()) {
            int toSend = Math.min(amount, getEnergy());
            if (toSend <= 0) {
                continue;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor instanceof AbstractMachineBlockEntity machine && machine != this
                    && machine.getEnergy() < machine.getMaxEnergy()) {
                int accepted = machine.receiveEnergy(Math.min(toSend, (int) machine.getMaxEnergy() - machine.getEnergy()));
                if (accepted > 0) {
                    consumeEnergy(accepted);
                }
            }
        }
    }
}
