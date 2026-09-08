package com.bentech.client.gui;

import com.bentech.BenTech;
import com.bentech.gui.CreativeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * GUI for the creative energy source. Shows the current output (EU/t) and
 * provides + / - buttons (which send a container-button packet) to adjust it.
 */
public class CreativeScreen extends AbstractContainerScreen<CreativeMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(BenTech.MOD_ID, "textures/gui/creative_gui.png");

    public CreativeScreen(CreativeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        // "+" increases output, "-" decreases output (container button ids 0 / 1).
        this.addRenderableWidget(Button.builder(Component.literal("+"),
                        btn -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0))
                .bounds(x + 96, y + 44, 16, 16).build());
        this.addRenderableWidget(Button.builder(Component.literal("-"),
                        btn -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1))
                .bounds(x + 96, y + 66, 16, 16).build());
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
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Output readout.
        int output = this.menu.getOutput();
        graphics.drawString(this.font, "Output: " + output + " EU/t", x + 20, y + 34, 0xFFFFFF);
        if (this.menu.isActive()) {
            graphics.drawString(this.font, "Feeding network", x + 20, y + 48, 0x54D62C);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Labels are drawn inside renderBg; keep this empty.
    }
}
