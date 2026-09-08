package com.bentech.gui;

import com.bentech.BenTech;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;

/**
 * Registers the {@link MenuType} used by all machine GUIs.
 */
public final class ModMenus {

    public static final MenuType<MachineMenu> MACHINE = Registry.register(
            BuiltInRegistries.MENU,
            BenTech.id("machine"),
            new MenuType<>(MachineMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final MenuType<CreativeMenu> CREATIVE = Registry.register(
            BuiltInRegistries.MENU,
            BenTech.id("creative_energy"),
            new MenuType<>(CreativeMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {
    }
}
