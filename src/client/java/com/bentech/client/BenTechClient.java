package com.bentech.client;

import com.bentech.client.gui.CreativeScreen;
import com.bentech.client.gui.MachineScreen;
import com.bentech.gui.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

/**
 * Client-side initialisation. Registers the machine GUI screen against its
 * menu type so right-clicking a machine opens a real, interactive window.
 */
public class BenTechClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenus.MACHINE, MachineScreen::new);
        MenuScreens.register(ModMenus.CREATIVE, CreativeScreen::new);
        LOGGER.info("BenTech client initialised.");
    }

    public static final org.slf4j.Logger LOGGER =
            org.slf4j.LoggerFactory.getLogger("bentech-client");
}
