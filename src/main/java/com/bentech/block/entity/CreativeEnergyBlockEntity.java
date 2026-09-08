package com.bentech.block.entity;

import com.bentech.gui.CreativeMenu;
import com.bentech.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Creative energy source. It never runs out: every tick it refills its buffer
 * and pushes an adjustable amount of EU/t into any adjacent machine or cable.
 * The output amount is adjustable from its GUI (+ / - buttons).
 */
public class CreativeEnergyBlockEntity extends AbstractMachineBlockEntity {

    private final ItemStack[] items = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

    /** Selectable output levels (EU per tick). */
    private static final int[] OUTPUTS = {32, 64, 128, 256, 512, 1024, 2048, 8192, 32768, 131072};
    private static final int DEFAULT_OUTPUT = 256;

    private int output = DEFAULT_OUTPUT;

    public static final int DATA_OUTPUT = 0;
    public static final int DATA_ACTIVE = 1;
    public static final int DATA_COUNT = 2;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_OUTPUT -> output;
                case DATA_ACTIVE -> getEnergy() > 0 ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // read-only mirrors of the block entity
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public CreativeEnergyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE, pos, state);
    }

    public int getOutput() {
        return output;
    }

    public void increaseOutput() {
        setOutputByIndex(indexOf(output) + 1);
    }

    public void decreaseOutput() {
        setOutputByIndex(indexOf(output) - 1);
    }

    private void setOutputByIndex(int idx) {
        idx = Math.max(0, Math.min(OUTPUTS.length - 1, idx));
        int next = OUTPUTS[idx];
        if (next != output) {
            output = next;
            setChanged();
        }
    }

    private static int indexOf(int value) {
        for (int i = 0; i < OUTPUTS.length; i++) {
            if (OUTPUTS[i] >= value) {
                return i;
            }
        }
        return OUTPUTS.length - 1;
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
        return 1_000_000_000L;
    }

    @Override
    public boolean isActive() {
        return output > 0;
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
    public ContainerData getData() {
        return data;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CreativeMenu(containerId, playerInventory, this, data);
    }

    @Override
    public void tickServer(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }
        // Creative: always stay full so it can feed endlessly.
        setEnergy((int) getMaxEnergy());
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
