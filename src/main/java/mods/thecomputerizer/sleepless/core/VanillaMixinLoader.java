package mods.thecomputerizer.sleepless.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.LOGGER;

public class VanillaMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    static {
        LOGGER.info("Initializing vanilla mixins");
    }
    
    @Override public String getAccessTransformerClass() {
        return null;
    }

    @Override public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    @Override public List<String> getMixinConfigs() {
        return Collections.singletonList("sleepless_vanilla.mixin.json");
    }

    @Override public String getModContainerClass() {
        return null;
    }

    @Override public @Nullable String getSetupClass() {
        return null;
    }

    @Override public void injectData(Map<String,Object> data) {}
}