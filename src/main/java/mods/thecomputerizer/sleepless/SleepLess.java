package mods.thecomputerizer.sleepless;

import mods.thecomputerizer.sleepless.capability.nightterror.INightTerrorCap;
import mods.thecomputerizer.sleepless.capability.nightterror.NightTerrorCap;
import mods.thecomputerizer.sleepless.capability.nightterror.NightTerrorCapStorage;
import mods.thecomputerizer.sleepless.capability.sleepdebt.ISleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebtStorage;
import mods.thecomputerizer.sleepless.client.SleepLessClient;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.network.*;
import mods.thecomputerizer.sleepless.util.AddedEnums;
import mods.thecomputerizer.sleepless.common.SleepLessCommands;
import mods.thecomputerizer.theimpossiblelibrary.TheImpossibleLibrary;
import mods.thecomputerizer.theimpossiblelibrary.network.NetworkHandler;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES)
public class SleepLess {

    private static boolean GUARUNTEED_RANDOMS = false;

    public SleepLess() {
        Constants.LOGGER.info("Started constructing mod class");
        AddedEnums.load();
        if(Constants.IS_DEV) {
            TheImpossibleLibrary.enableDevLog();
            GUARUNTEED_RANDOMS = true;
        }
        NetworkHandler.queueClientPacketRegistries(PacketRenderTests.class,PacketUpdateClientEffects.class,
                PacketUpdateNightTerrorClient.class, PacketSendWorldSound.class);
        Constants.LOGGER.info("Constructed mod class");
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        Constants.LOGGER.info("Starting common pre-init");
        CapabilityManager.INSTANCE.register(ISleepDebt.class,new SleepDebtStorage(),SleepDebt::new);
        CapabilityManager.INSTANCE.register(INightTerrorCap.class,new NightTerrorCapStorage(),NightTerrorCap::new);
        if(isClient()) SleepLessClient.preInit(event);
        Constants.LOGGER.info("Completed common pre-init");
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        Constants.LOGGER.info("Starting common post-init");
        if(isClient()) SleepLessClient.postInit(event);
        Constants.LOGGER.info("Completed common post-init");
    }

    @Mod.EventHandler
    public void start(FMLServerStartingEvent event) {
        Constants.LOGGER.info("Handling server starting");
        event.registerServerCommand(new SleepLessCommands());
        Constants.LOGGER.info("Handled server starting");
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
