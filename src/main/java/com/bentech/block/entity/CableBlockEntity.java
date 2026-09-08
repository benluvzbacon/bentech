package com.bentech.block.entity;

import com.bentech.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A power cable block entity. It has a modest energy buffer and on each tick
 * redistributes stored EU to any adjacent machine that is short on energy,
 * letting a single generator power a whole network of machines.
 */
public class CableBlockEntity extends AbstractMachineBlockEntity {

    private final ItemStack[] items = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE, pos, state);
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
        return 8192;
    }

    @Override
    public boolean isActive() {
        return getEnergy() > 0;
    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int getDuration() {
        return 1;
    }

    @Override
    public void tickServer(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }
        distribute(level, pos);
    }

    private void distribute(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            int energy = getEnergy();
            if (energy <= 0) {
                return;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor instanceof AbstractMachineBlockEntity machine && machine != this
                    && machine.getEnergy() < machine.getMaxEnergy()) {
                int space = (int) machine.getMaxEnergy() - machine.getEnergy();
                int toSend = Math.min(energy, space);
                int accepted = machine.receiveEnergy(toSend);
                if (accepted > 0) {
                    consumeEnergy(accepted);
                }
            }
        }
    }
}
