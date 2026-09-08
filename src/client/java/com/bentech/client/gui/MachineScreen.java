package com.bentech.client.gui;

import com.bentech.BenTech;
import com.bentech.gui.MachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * In-game GUI for a machine. Draws a themed background, the machine's input /
 * output slots (provided by the menu), a live energy buffer bar and a process
 * progress bar. Slots themselves are rendered by {@link AbstractContainerScreen}.
 */
public class MachineScreen extends AbstractContainerScreen<MachineMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(BenTech.MOD_ID, "textures/gui/machine_gui.png");

    public MachineScreen(MachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Background panel.
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Energy bar (vertical) - frame drawn in texture, fill drawn here.
        int maxEnergy = this.menu.getMaxEnergy();
        int energy = this.menu.getEnergy();
        if (maxEnergy > 0) {
            float energyRatio = Math.min(1.0f, (float) energy / (float) maxEnergy);
            int barHeight = (int) (30 * energyRatio);
            int color = energyRatio < 0.25f ? 0xFFD42B2B : (energyRatio < 0.6f ? 0xFFF2D230 : 0xFF54D62C);
            // Vertical energy gauge at (x+10, y+28).
            graphics.fill(x + 11, y + 28 + (30 - barHeight), x + 19, y + 56, color);
            graphics.drawString(this.font, "EU", x + 8, y + 60, 0xFFFFFF);
        }

        // Process progress (horizontal bar) near the arrow region.
        int duration = this.menu.getDuration();
        int progress = this.menu.getProgress();
        if (duration > 0 && progress > 0) {
            float ratio = Math.min(1.0f, (float) progress / (float) duration);
            int barWidth = (int) (22 * ratio);
            graphics.fill(x + 88, y + 33, x + 88 + barWidth, y + 39, 0xFFF2D230);
        }

        // Title + energy readout.
        graphics.drawString(this.font, this.title, x + 8, y + 6, 0xFFFFFF);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Energy readout is drawn inside renderBg so it aligns with the panel.
    }
}
