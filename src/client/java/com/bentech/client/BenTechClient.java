package com.bentech.client;

import net.fabricmc.api.ClientModInitializer;

/**
 * Client-side initialisation. BenTech currently has no client-only rendering;
 * this exists so the mod has a proper client entrypoint and can be extended
 * with machine GUI screens and block entity renderers later.
 */
public class BenTechClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BenTechClient.LOGGER.info("BenTech client initialised.");
    }

    public static final org.slf4j.Logger LOGGER =
            org.slf4j.LoggerFactory.getLogger("bentech-client");
}
