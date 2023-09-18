package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.ParticleRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SleepLessClient {

    public static void preInit(FMLPreInitializationEvent event) {
        Constants.LOGGER.info("Starting client pre-init");
        ClientRegistryHandler.registerRenderers();
        Constants.LOGGER.info("Completed client pre-init");
    }

    public static void postInit(FMLPostInitializationEvent event) {
        Constants.LOGGER.info("Starting client post-init");
        ParticleRegistry.postInit();
        Constants.LOGGER.info("Completed client post-init");
    }
}
