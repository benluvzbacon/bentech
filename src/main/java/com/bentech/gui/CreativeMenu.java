package com.bentech.gui;

import com.bentech.block.entity.CreativeEnergyBlockEntity;
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
 * Menu for the creative energy source. It shows the current output (EU/t) and
 * lets the player adjust it with the GUI's + / - buttons (via
 * {@link #clickMenuButton(Player, int)}). Only the player inventory is shown.
 */
public class CreativeMenu extends AbstractContainerMenu {

    private static final int PLAYER_INV_START = 0;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_END = PLAYER_INV_END + 9;

    private final CreativeEnergyBlockEntity blockEntity;
    private final Container inventory;
    private final ContainerData data;

    // Client constructor (an empty placeholder is synced from the server).
    public CreativeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null,
                new SimpleContainerData(CreativeEnergyBlockEntity.DATA_COUNT));
    }

    // Server constructor.
    public CreativeMenu(int containerId, Inventory playerInventory,
                        CreativeEnergyBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.CREATIVE, containerId);
        this.blockEntity = blockEntity;
        this.inventory = new SimpleContainer(0);
        this.data = data;

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

    public int getOutput() {
        return data.get(CreativeEnergyBlockEntity.DATA_OUTPUT);
    }

    public boolean isActive() {
        return data.get(CreativeEnergyBlockEntity.DATA_ACTIVE) > 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity == null || blockEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // The creative energy source has no item slots; nothing to move.
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (blockEntity == null) {
            return false;
        }
        if (buttonId == 0) {
            blockEntity.increaseOutput();
        } else if (buttonId == 1) {
            blockEntity.decreaseOutput();
        } else {
            return false;
        }
        return true;
    }
}
