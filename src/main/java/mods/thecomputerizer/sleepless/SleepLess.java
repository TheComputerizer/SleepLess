package mods.thecomputerizer.sleepless;

import mods.thecomputerizer.sleepless.capability.nightterror.INightTerrorCap;
import mods.thecomputerizer.sleepless.capability.nightterror.NightTerrorCap;
import mods.thecomputerizer.sleepless.capability.nightterror.NightTerrorCapStorage;
import mods.thecomputerizer.sleepless.capability.sleepdebt.ISleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebtStorage;
import mods.thecomputerizer.sleepless.client.SleepLessClient;
import mods.thecomputerizer.sleepless.network.*;
import mods.thecomputerizer.sleepless.util.AddedEnums;
import mods.thecomputerizer.sleepless.common.SleepLessCommands;
import mods.thecomputerizer.theimpossiblelibrary.api.core.CoreAPI;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.*;
import static net.minecraftforge.common.capabilities.CapabilityManager.INSTANCE;

@Mod(modid=MODID,name=NAME,version=VERSION,dependencies=DEPENDENCIES)
public class SleepLess {

    private static boolean GUARUNTEED_RANDOMS = false;

    public SleepLess() {
        LOGGER.info("Started constructing mod class");
        AddedEnums.load();
        if(IS_DEV) GUARUNTEED_RANDOMS = true;
        SleepLessNetwork.initCommon();
        if(CoreAPI.isClient()) SleepLessNetwork.initClient();
        LOGGER.info("Constructed mod class");
    }

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Starting common pre-init");
        INSTANCE.register(ISleepDebt.class,new SleepDebtStorage(),SleepDebt::new);
        INSTANCE.register(INightTerrorCap.class,new NightTerrorCapStorage(),NightTerrorCap::new);
        if(isClient()) SleepLessClient.preInit(event);
        LOGGER.info("Completed common pre-init");
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Starting common post-init");
        if(isClient()) SleepLessClient.postInit(event);
        LOGGER.info("Completed common post-init");
    }

    @EventHandler
    public void start(FMLServerStartingEvent event) {
        LOGGER.info("Handling server starting");
        event.registerServerCommand(new SleepLessCommands());
        LOGGER.info("Handled server starting");
    }

    private static boolean isClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    public static double fudgeDouble(double val, double max) {
        return GUARUNTEED_RANDOMS ? max : val;
    }

    public static float fudgeFloat(float val, float max) {
        return GUARUNTEED_RANDOMS ? max : val;
    }

    public static int fudgeInt(int val, int max) {
        return GUARUNTEED_RANDOMS ? max : val;
    }
}