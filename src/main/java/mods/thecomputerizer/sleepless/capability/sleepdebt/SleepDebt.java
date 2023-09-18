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

    private void updateEffects() {
        this.grayScale = MathHelper.clamp(this.debt-9f,0f,1f);
    }

    @Override
    public void sync(EntityPlayerMP player) {
        updateEffects();
        new PacketUpdateClientEffects(this.grayScale).addPlayers(player).send();
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
