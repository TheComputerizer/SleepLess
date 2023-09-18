package mods.thecomputerizer.sleepless.capability.sleepdebt;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.network.PacketUpdateClientEffects;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class SleepDebt implements ISleepDebt {

    private float debt = 0f;
    private float grayScale = 0f;
    private float hungerAmplifier = 0f;
    private float ambientSoundChance = 0f; //chance out of 100 every second
    private float mobSpawnAmplifier = 0f;
    private float quietSounds = 0f;
    private float lightDimming = 0f;
    private float fogDensity = 0f;
    private float speedFactor = 1f;
    private float breathingFactor = 0f;
    private float miningSpeed = 1f;

    @Override
    public boolean onTicksSlept(long ticks) {
        boolean shouldAdjust = ticks<5000 || this.debt>0;
        if(shouldAdjust) {
            Constants.testLog("CURRENT DEBT IS {}", this.debt);
            Constants.testLog("{} TICKS WERE SLEPT", ticks);
            float addedDebt = SleepLessConfigHelper.getAddedDebt(ticks);
            Constants.testLog("{} DEBT IS BEING ADDED", addedDebt);
            this.debt = Math.max(0f, this.debt + addedDebt);
            Constants.testLog("CURRENT DEBT IS NOW {}", this.debt);
        }
        return shouldAdjust;
    }

    @Override
    public float getDebt() {
        return this.debt;
    }

    @Override
    public void setDebt(EntityPlayerMP player, float debt) {
        this.debt = debt;
        sync(player);
    }

    @Override
    public float getHungerAmplifier() {
        return this.hungerAmplifier;
    }

    @Override
    public float getMiningSpeedFactor() {
        return this.miningSpeed;
    }

    private void updateEffects() {
        this.grayScale = MathHelper.clamp(this.debt-9f,0f,1f);
        this.hungerAmplifier = this.debt>=2f ? this.debt>=4 ? 1f : 0.5f : 0f;
        this.ambientSoundChance = this.debt>=2f ? (this.debt-2f)*0.75f : 0f;
        this.mobSpawnAmplifier = this.debt>=3f ? this.debt>=6f ? this.debt>=9f ? 0.75f : 0.5f : 0.25f : 0f;
        this.quietSounds = this.debt>=6f ? this.debt>=8f ? this.debt>=9f ? 0.25f : 0.5f : 0.75f : 1f;
        this.lightDimming = this.debt>=8f ? 0.5f : 0f;
        this.fogDensity = this.debt>=1f ? (this.debt-1f)/10f : 0f;
        this.speedFactor = this.debt>=7f ? this.debt>=10f ? 2f : 1.5f : 1f;
        this.breathingFactor = this.debt>=2 ? this.debt>=5 ? this.debt>=8 ? this.debt<=10 ? 0.4f : 0.3f : 0.2f : 0.1f : 0f;
        this.miningSpeed = this.debt>=6f ? this.debt>=10f ? 0.5f-((this.debt-10f)/180f) : 0.75f : 1f;
    }

    @Override
    public void sync(EntityPlayerMP player) {
        updateEffects();
        new PacketUpdateClientEffects(this.grayScale,this.ambientSoundChance,this.quietSounds,this.lightDimming,
                this.fogDensity,this.speedFactor,this.breathingFactor,this.miningSpeed).addPlayers(player).send();
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag  = new NBTTagCompound();
        tag.setFloat("debt",this.debt);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.debt = tag.getFloat("debt");
        updateEffects();
    }
}
