package mods.thecomputerizer.sleepless.config;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorClient;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

public class SleepLessConfigHelper {

    private static Block[] PHASED_BLOCK_BLACKLIST = new Block[]{Blocks.BEDROCK,Blocks.BARRIER};
    private static Block[] PHANTOM_PATHFIND_BLACKLIST = new Block[]{Blocks.BEDROCK,Blocks.BARRIER};
    private static boolean NEEDS_CACHING = true;

    public static void onConfigReloaded() {
        NEEDS_CACHING = true;
    }

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

    public static float nightTerrorChance(EntityPlayerMP player) {
        SleepLessConfig.NightTerror nightTerror = SleepLessConfig.NIGHT_TERROR;
        float sleepDebt = CapabilityHandler.getSleepDebt(player);
        if(sleepDebt<nightTerror.minSleepDebt) return -1;
        float debtIncrements = (sleepDebt-nightTerror.minSleepDebt)/nightTerror.sleepDebtIncrement;
        return nightTerror.minChance+(debtIncrements*nightTerror.chanceIncrement);
    }

    public static float calculateFinalChance(Collection<Float> chances) {
        switch(SleepLessConfig.NIGHT_TERROR.serverChanceFormula) {
            case "HIGHEST": return Collections.max(chances)/100f;
            case "LOWEST": return Collections.min(chances)/100f;
            default: {
                float total = 0f;
                for(Float chance : chances) total+=chance;
                return (total/chances.size())/100f;
            }
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
        return NightTerrorClient.overrideQuietSound(!client.disableClientEffects && !client.disableAudioEffects && !client.disableAmbientSounds);
    }

    public static boolean shouldMuffleSounds() {
        SleepLessConfig.ClientEffects client = SleepLessConfig.CLIENT_EFFECTS;
        return NightTerrorClient.overrideQuietSound(!client.disableClientEffects && !client.disableAudioEffects && !client.disableSoundMuffler);
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

    private static void cacheBlocks() {
        SleepLessConfig.Phantom phantom = SleepLessConfig.PHANTOM;
        Set<Block> cachedBlocks = new HashSet<>();
        PHASED_BLOCK_BLACKLIST = addToBlockCache(cachedBlocks,phantom.phasedBlacklist).toArray(new Block[0]);
        PHANTOM_PATHFIND_BLACKLIST = addToBlockCache(cachedBlocks,phantom.pathfindBlacklist).toArray(new Block[0]);
        NEEDS_CACHING = false;
    }

    private static Set<Block> addToBlockCache(Set<Block> blocks, String ... blockNames) {
        for(String blockName : blockNames) {
            ResourceLocation blockRes = new ResourceLocation(blockName);
            if(ForgeRegistries.BLOCKS.containsKey(blockRes)) blocks.add(ForgeRegistries.BLOCKS.getValue(blockRes));
        }
        return blocks;
    }

    public static Block[] getPhasedBlockBlacklist() {
        if(NEEDS_CACHING) cacheBlocks();
        return Arrays.copyOf(PHASED_BLOCK_BLACKLIST,PHASED_BLOCK_BLACKLIST.length);
    }

    public static Block[] getPhantomPathfindBlacklist() {
        if(NEEDS_CACHING) cacheBlocks();
        return Arrays.copyOf(PHANTOM_PATHFIND_BLACKLIST,PHANTOM_PATHFIND_BLACKLIST.length);
    }
}
