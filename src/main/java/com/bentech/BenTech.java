package com.bentech;

import com.bentech.gui.ModMenus;
import com.bentech.registry.ModBlockEntities;
import com.bentech.registry.ModBlocks;
import com.bentech.registry.ModCreativeTab;
import com.bentech.registry.ModItems;
import com.bentech.util.MachineRecipes;
import com.bentech.world.ModWorldgen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BenTech — a GregTech-inspired modification for Fabric 1.21.1.
 *
 * Progression loop:
 * <ol>
 *   <li>Mine ore (or obtain it) and feed it to a Macerator to get dust.</li>
 *   <li>Smelt dust into ingots in the Electric Furnace.</li>
 *   <li>Compress ingots into plates and craft machine components.</li>
 *   <li>Combine raw metals in the Alloy Smelter to make bronze, steel, brass…</li>
 *   <li>Power everything with the Generator and rise through the voltage tiers.</li>
 * </ol>
 */
public class BenTech implements ModInitializer {

    public static final String MOD_ID = "bentech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.load();
        ModBlocks.load();
        // Touch static holders so block entities and the menu type register now.
        LOGGER.info("Registered block entity types: {}", ModBlockEntities.PROCESSING);
        LOGGER.info("Registered machine menu: {}", ModMenus.MACHINE);
        LOGGER.info("Registered creative energy: block={} menu={}",
                ModBlocks.CREATIVE_ENERGY.getDescriptionId(), ModMenus.CREATIVE);
        MachineRecipes.build();
        ModCreativeTab.load();
        ModWorldgen.register();
        LOGGER.info("BenTech initialised.");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
