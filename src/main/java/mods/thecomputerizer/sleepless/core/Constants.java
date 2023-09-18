package mods.thecomputerizer.sleepless.core;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {
    public static final String MODID = "sleepless";
    public static final String NAME = "SleepLess";
    public static final String VERSION = "0.0.1";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2860,);required-after:mixinbooter;" +
            "required-after:theimpossiblelibrary;";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static final boolean IS_DEV = true;

    public static void testLog(String msg, Object ... parameters) {
        if(IS_DEV) LOGGER.error(msg,parameters);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MODID,path);
    }
}
