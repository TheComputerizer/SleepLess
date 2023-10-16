package mods.thecomputerizer.sleepless.config;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraftforge.common.config.Config.*;

@Mod.EventBusSubscriber(modid = Constants.MODID)
@Config(modid = Constants.MODID, name = Constants.NAME, category = "")
public class SleepLessConfig {

    @Name("sleepdebttimings")
    @LangKey("config.sleepless.sleepdebttimings")
    public static SleepDebtTimings SLEEP_DEBT_TIMINGS = new SleepDebtTimings();

    @Name("napping")
    @LangKey("config.sleepless.napping")
    public static Napping NAPPING = new Napping();

    @Name("statuseffects")
    @LangKey("config.sleepless.statuseffects")
    public static StatusEffects STATUS_EFFECTS = new StatusEffects();

    @Name("nightterror")
    @LangKey("config.sleepless.nightterror")
    public static NightTerror NIGHT_TERROR = new NightTerror();

    @Name("client")
    @LangKey("config.sleepless.client")
    public static ClientEffects CLIENT_EFFECTS = new ClientEffects();

    public static class SleepDebtTimings {

        @Name("maxDaysLost")
        @LangKey("config.sleepless.sleepdebttimings.maxDaysLost")
        public float maxDaysLost = 1f;

        @Name("maxDaysPaid")
        @LangKey("config.sleepless.sleepdebttimings.maxDaysPaid")
        public float maxDaysPaid = 3f;
    }

    public static class Napping {

        @Name("disableNapping")
        @LangKey("config.sleepless.napping.disableNapping")
        public boolean disableNapping = false;

        @Name("napTime")
        @LangKey("config.sleepless.napping.napTime")
        public int napTime = 2;

        @Name("napAmount")
        @LangKey("config.sleepless.napping.napAmount")
        public float napAmount = 0.25f;

        @Name("napDay")
        @LangKey("config.sleepless.napping.napDay")
        public float napDay = 5f;
    }

    public static class StatusEffects {

        @Name("disableStatusEffects")
        @LangKey("config.sleepless.statuseffects.disableStatusEffects")
        public boolean disableStatusEffects = false;

        @Name("disableHunger")
        @LangKey("config.sleepless.statuseffects.disableHunger")
        public boolean disableHunger = false;

        @Name("disableSlowness")
        @LangKey("config.sleepless.statuseffects.disableSlowness")
        public boolean disableSlowness = false;

        @Name("disableMiningFatigue")
        @LangKey("config.sleepless.statuseffects.disableMiningFatigue")
        public boolean disableMiningFatigue = false;

        @Name("disableFasterDrowning")
        @LangKey("config.sleepless.statuseffects.disableFasterDrowning")
        public boolean disableFasterDrowning = false;
    }

    public static class NightTerror {

        @Name("disableNightTerrors")
        @LangKey("config.sleepless.nightterror.disableNightTerrors")
        public boolean disableNightTerrors = false;

        @Name("minSleepDebt")
        @LangKey("config.sleepless.nightterror.minSleepDebt")
        public float minSleepDebt = 10f;

        @Name("minChance")
        @LangKey("config.sleepless.nightterror.minChance")
        public float minChance = 50f;

        @Name("chanceIncrement")
        @LangKey("config.sleepless.nightterror.chanceIncrement")
        public float chanceIncrement = 10f;

        @Name("sleepDebtIncrement")
        @LangKey("config.sleepless.nightterror.sleepDebtIncrement")
        public float sleepDebtIncrement = 1f;

        @Name("serverChanceFormula")
        @LangKey("config.sleepless.nightterror.serverChanceFormula")
        public String serverChanceFormula = "AVERAGE";
    }

    public static class ClientEffects {

        @Name("disableClientEffects")
        @LangKey("config.sleepless.client.disableClientEffects")
        public boolean disableClientEffects = false;

        @Name("disableAudioEffects")
        @LangKey("config.sleepless.client.disableAudioEffects")
        public boolean disableAudioEffects = false;

        @Name("disableAmbientSounds")
        @LangKey("config.sleepless.client.disableAmbientSounds")
        public boolean disableAmbientSounds = false;

        @Name("disableSoundMuffler")
        @LangKey("config.sleepless.client.disableSoundMuffler")
        public boolean disableSoundMuffler = false;

        @Name("disableVisualEffects")
        @LangKey("config.sleepless.client.disableVisualEffects")
        public boolean disableVisualEffects = false;

        @Name("disableDistortion")
        @LangKey("config.sleepless.client.disableDistortion")
        public boolean disableDistortion = false;

        @Name("disableHallucinations")
        @LangKey("config.sleepless.client.disableHallucinations")
        public boolean disableHallucinations = false;

        @Name("disableGrayscale")
        @LangKey("config.sleepless.client.disableGrayscale")
        public boolean disableGrayscale = false;

        @Name("disableHeavyBreathing")
        @LangKey("config.sleepless.client.disableHeavyBreathing")
        public boolean disableHeavyBreathing = true;

        @Name("disableFog")
        @LangKey("config.sleepless.client.disableFog")
        public boolean disableFog = false;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(event.getModID().equals(Constants.MODID))
            ConfigManager.sync(event.getModID(),Config.Type.INSTANCE);
    }
}
