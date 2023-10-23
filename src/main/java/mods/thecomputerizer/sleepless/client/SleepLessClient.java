package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.ParticleRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public class SleepLessClient {

    @SideOnly(Side.CLIENT)
    public static void preInit(FMLPreInitializationEvent event) {
        Constants.LOGGER.info("Starting client pre-init");
        Constants.LOGGER.info("Completed client pre-init");
    }

    @SideOnly(Side.CLIENT)
    public static void postInit(FMLPostInitializationEvent event) {
        Constants.LOGGER.info("Starting client post-init");
        ParticleRegistry.postInit();
        Constants.LOGGER.info("Completed client post-init");
    }

    @SideOnly(Side.CLIENT)
    public static float getClientEffect(int selector, float defaultVal) {
        switch(selector) {
            case 0: return ClientEffects.FOV_ADJUST;
            case 1: return ClientEffects.BREATHING_FACTOR;
            case 2: return ClientEffects.COLOR_CORRECTION;
            case 3: return ClientEffects.AMBIENT_SOUND_CHANCE;
            case 4: return ClientEffects.SCREEN_SHAKE;
            case 5: return ClientEffects.QUIET_SOUNDS;
            case 6: return ClientEffects.LIGHT_DIMMING;
            case 7: return ClientEffects.FOG_DENSITY;
            case 8: return ClientEffects.WALK_SPEED;
            case 9: return ClientEffects.MINING_SPEED;
            case 10: return ClientEffects.PHANTOM_VISIBILITY;
            default: return defaultVal;
        }
    }
}
