package com.bentech.gui;

import com.bentech.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Server/client synchronized menu for a machine. Presents the machine's two
 * input slots and one output slot, plus the player inventory, and syncs the
 * energy buffer and process progress to the client via {@link ContainerData}.
 */
public class MachineMenu extends AbstractContainerMenu {

    private static final int MACHINE_SLOTS = AbstractMachineBlockEntity.INVENTORY_SIZE;
    private static final int PLAYER_INV_START = MACHINE_SLOTS;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_END = PLAYER_INV_END + 9;

    private final Container inventory;
    private final ContainerData data;

    // Client constructor (empty placeholder containers are synced from server).
    public MachineMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(MACHINE_SLOTS),
                new SimpleContainerData(AbstractMachineBlockEntity.DATA_COUNT));
    }

    // Server constructor.
    public MachineMenu(int containerId, Inventory playerInventory, Container inventory, ContainerData data) {
        super(ModMenus.MACHINE, containerId);
        checkContainerSize(inventory, MACHINE_SLOTS);
        this.inventory = inventory;
        this.data = data;
        inventory.startOpen(playerInventory.player);

        // Machine slots: input A, input B, output.
        this.addSlot(new Slot(inventory, 0, 45, 27));
        this.addSlot(new Slot(inventory, 1, 63, 27));
        this.addSlot(new Slot(inventory, 2, 115, 27));

        // Player inventory (3 rows).
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Player hotbar.
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getEnergy() {
        return data.get(AbstractMachineBlockEntity.DATA_ENERGY);
    }

    public int getMaxEnergy() {
        return data.get(AbstractMachineBlockEntity.DATA_MAX_ENERGY);
    }

    public int getProgress() {
        return data.get(AbstractMachineBlockEntity.DATA_PROGRESS);
    }

    public int getDuration() {
        return data.get(AbstractMachineBlockEntity.DATA_DURATION);
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < MACHINE_SLOTS) {
                // Move machine output/inputs into player inventory.
                if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move player items into the machine's input slots.
                if (!this.moveItemStackTo(stack, 0, MACHINE_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return result;
    }
}
