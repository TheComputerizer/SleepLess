package mods.thecomputerizer.sleepless.config;

import net.minecraft.util.math.MathHelper;

public class SleepLessConfigHelper {

    private static float getMaxPaid() {
        return -1f*SleepLessConfig.SLEEP_DEBT_TIMINGS.maxDaysPaid;
    }

    private static float getMaxLost() {
        return SleepLessConfig.SLEEP_DEBT_TIMINGS.maxDaysLost;
    }

    public static float getAddedDebt(long ticks) {
        float addedDebt = getAddedDebtInner(Math.max(0f, ((float) ticks) / 1000f));
        return MathHelper.clamp(addedDebt,getMaxPaid(),getMaxLost());
    }

    private static float getAddedDebtInner(float hours) {
        int rounded = (int)hours;
        float t1 = 1f/3f;
        float t2 = 2f/3f;
        switch (rounded) {
            case 0 : return 0.8f+(0.2f*(1-hours));
            case 1 : return t2+((0.8f-t2)*(2-hours));
            case 2 : return t1+(t1*(3-hours));
            case 3 : return 0.2f+((t1-0.2f)*(4-hours));
            case 4 : return 0.2f*(5-hours);
            case 5 : return -0.5f*(hours-5);
            case 6 : return -0.5f-(0.5f*(hours-6));
            case 7 : return -1f-(0.5f*(hours-7));
            case 8 : return -1.5f-(0.5f*(hours-8));
            default : return -2f-((hours-9)/3f);
        }
    }

    public static boolean shouldBeHungry() {
        SleepLessConfig.StatusEffects effects = SleepLessConfig.STATUS_EFFECTS;
        return !effects.disableStatusEffects && !effects.disableHunger;
    }

    public static boolean shouldMineSlower() {
        SleepLessConfig.StatusEffects effects = SleepLessConfig.STATUS_EFFECTS;
        return !effects.disableStatusEffects && !effects.disableMiningFatigue;
    }

    public static boolean shouldWalkSlower() {
        SleepLessConfig.StatusEffects effects = SleepLessConfig.STATUS_EFFECTS;
        return !effects.disableStatusEffects && !effects.disableSlowness;
    }

    public static boolean shouldBreatheHeavily() {
        SleepLessConfig.ClientEffects client = SleepLessConfig.CLIENT_EFFECTS;
        return !client.disableClientEffects && !client.disableVisualEffects && !client.disableHeavyBreathing;
    }

    public static boolean shouldPlaySounds() {
        SleepLessConfig.ClientEffects client = SleepLessConfig.CLIENT_EFFECTS;
        return !client.disableClientEffects && !client.disableAudioEffects && !client.disableAmbientSounds;
    }

    public static boolean shouldMuffleSounds() {
        SleepLessConfig.ClientEffects client = SleepLessConfig.CLIENT_EFFECTS;
        return !client.disableClientEffects && !client.disableAudioEffects && !client.disableSoundMuffler;
    }

    public static boolean shouldIncreaseFog() {
        SleepLessConfig.ClientEffects client = SleepLessConfig.CLIENT_EFFECTS;
        return !client.disableClientEffects && !client.disableVisualEffects && !client.disableFog;
    }

    public static boolean shouldLoseColor() {
        SleepLessConfig.ClientEffects client = SleepLessConfig.CLIENT_EFFECTS;
        return !client.disableClientEffects && !client.disableVisualEffects && !client.disableGrayscale;
    }

    public static boolean shouldDimLight() {
        SleepLessConfig.ClientEffects client = SleepLessConfig.CLIENT_EFFECTS;
        return !client.disableClientEffects && !client.disableVisualEffects;
    }
}
