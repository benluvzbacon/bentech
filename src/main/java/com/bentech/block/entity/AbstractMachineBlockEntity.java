package com.bentech.block.entity;

import com.bentech.gui.MachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared machinery base. Every machine holds a small internal "EU" energy
 * buffer and a fixed-size inventory. Slots 0/1 are inputs, slot 2 is output
 * (for generators slot 0 is the fuel input). Subclasses implement the per-tick
 * behaviour. Implements {@link MenuProvider} so right-clicking a machine opens
 * an in-game GUI showing its contents, energy buffer and progress.
 */
public abstract class AbstractMachineBlockEntity extends BlockEntity implements Container, MenuProvider {

    /** Input A / fuel. */
    public static final int SLOT_INPUT_0 = 0;
    /** Input B (alloy smelter). */
    public static final int SLOT_INPUT_1 = 1;
    /** Output. */
    public static final int SLOT_OUTPUT = 2;
    /** Total number of slots for all machines. */
    public static final int INVENTORY_SIZE = 3;

    protected int energy;

    // Container data indices, synced to the client GUI.
    public static final int DATA_ENERGY = 0;
    public static final int DATA_MAX_ENERGY = 1;
    public static final int DATA_PROGRESS = 2;
    public static final int DATA_DURATION = 3;
    public static final int DATA_COUNT = 4;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_ENERGY -> energy;
                case DATA_MAX_ENERGY -> (int) getMaxEnergy();
                case DATA_PROGRESS -> getProgress();
                case DATA_DURATION -> getDuration();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // data slots are read-only mirrors of the block entity state
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    protected AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ------------------------------------------------------------------energy

    public abstract long getMaxEnergy();

    public int getEnergy() {
        return energy;
    }

    public boolean hasEnergy(int amount) {
        return energy >= amount;
    }

    public void setEnergy(int value) {
        int next = Math.max(0, Math.min((int) getMaxEnergy(), value));
        if (next != energy) {
            energy = next;
            setChanged();
        }
    }

    public void addEnergy(int amount) {
        setEnergy(energy + amount);
    }

    public void consumeEnergy(int amount) {
        setEnergy(energy - amount);
    }

    /** Returns the amount accepted (0 if the buffer is full). */
    public int receiveEnergy(int maxReceive) {
        int space = (int) getMaxEnergy() - energy;
        int accepted = Math.min(space, maxReceive);
        addEnergy(accepted);
        return accepted;
    }

    // ------------------------------------------------------------ processing

    public abstract boolean isActive();

    /** Renders the GUI progress / fuel amount (0 when idle). */
    public abstract int getProgress();

    /** Full progress / fuel duration in ticks (>= 1). */
    public abstract int getDuration();

    /** Runs once per server tick. */
    public abstract void tickServer(Level level, BlockPos pos, BlockState state);

    public ContainerData getData() {
        return containerData;
    }

    // ------------------------------------------------------------ MenuProvider

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public com.bentech.gui.MachineMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MachineMenu(containerId, playerInventory, this, this.getData());
    }

    // ------------------------------------------------------------ Container

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case SLOT_INPUT_0 -> getSlotItem(SLOT_INPUT_0);
            case SLOT_INPUT_1 -> getSlotItem(SLOT_INPUT_1);
            case SLOT_OUTPUT -> getSlotItem(SLOT_OUTPUT);
            default -> ItemStack.EMPTY;
        };
    }

    /** Subclasses own the actual item array. */
    protected abstract ItemStack getSlotItem(int slot);

    protected abstract void setSlotItem(int slot, ItemStack stack);

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack current = getItem(slot);
        if (current.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int taken = Math.min(amount, current.getCount());
        ItemStack result = current.copy();
        result.setCount(taken);
        current.shrink(taken);
        if (current.isEmpty()) {
            setSlotItem(slot, ItemStack.EMPTY);
        }
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack current = getItem(slot);
        setSlotItem(slot, ItemStack.EMPTY);
        setChanged();
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(Math.min(copy.getCount(), getMaxStackSize()));
        setSlotItem(slot, copy);
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null) {
            return false;
        }
        return this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(
                        this.worldPosition.getX() + 0.5,
                        this.worldPosition.getY() + 0.5,
                        this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setSlotItem(i, ItemStack.EMPTY);
        }
    }

    // ------------------------------------------------------------------- NBT

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energy);
        ListTag list = new ListTag();
        for (int i = 0; i < getContainerSize(); i++) {
            CompoundTag itemTag = new CompoundTag();
            ItemStack stack = getSlotItem(i);
            if (!stack.isEmpty()) {
                stack.save(registries, itemTag);
            }
            list.add(itemTag);
        }
        tag.put("items", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("energy");
        ListTag list = tag.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < getContainerSize(); i++) {
            if (i >= list.size()) {
                setSlotItem(i, ItemStack.EMPTY);
                continue;
            }
            CompoundTag itemTag = list.getCompound(i);
            setSlotItem(i, ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY));
        }
    }

    // ---------------------------------------------------------------- ticker

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractMachineBlockEntity blockEntity) {
        blockEntity.tickServer(level, pos, state);
    }
}
