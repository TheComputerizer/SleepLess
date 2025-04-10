package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.registry.ParticleRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mods.thecomputerizer.sleepless.client.render.ClientEffects.*;
import static mods.thecomputerizer.sleepless.core.SleepLessRef.LOGGER;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SuppressWarnings("unused")
public class SleepLessClient {

    @SideOnly(CLIENT)
    public static void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Starting client pre-init");
        LOGGER.info("Completed client pre-init");
    }

    @SideOnly(CLIENT)
    public static void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Starting client post-init");
        ParticleRegistry.postInit();
        LOGGER.info("Completed client post-init");
    }

    @SideOnly(CLIENT)
    public static float getClientEffect(int selector, float defaultVal) {
        switch(selector) {
            case 0: return FOV_ADJUST;
            case 1: return BREATHING_FACTOR;
            case 2: return COLOR_CORRECTION;
            case 3: return AMBIENT_SOUND_CHANCE;
            case 4: return SCREEN_SHAKE;
            case 5: return QUIET_SOUNDS;
            case 6: return LIGHT_DIMMING;
            case 7: return FOG_DENSITY;
            case 8: return WALK_SPEED;
            case 9: return MINING_SPEED;
            case 10: return PHANTOM_VISIBILITY;
            default: return defaultVal;
        }
    }
}