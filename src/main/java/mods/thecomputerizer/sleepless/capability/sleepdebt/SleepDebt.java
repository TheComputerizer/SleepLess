package mods.thecomputerizer.sleepless.capability.sleepdebt;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class SleepDebt implements ISleepDebt {

    private float debt = 0f;

    @Override
    public void onTicksSlept(long ticks) {
        boolean shouldAdjust = ticks<5000 || this.debt>0;
        if(shouldAdjust) {
            Constants.testLog("CURRENT DEBT IS {}", this.debt);
            Constants.testLog("{} TICKS WERE SLEPT", ticks);
            float addedDebt = SleepLessConfigHelper.getAddedDebt(ticks);
            Constants.testLog("{} DEBT IS BEING ADDED", addedDebt);
            this.debt = Math.max(0f, this.debt + addedDebt);
            Constants.testLog("CURRENT DEBT IS NOW {}", this.debt);
        }
    }

    @Override
    public float getDebt() {
        return this.debt;
    }

    private float getAddedDebt(float hours) {
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

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag  = new NBTTagCompound();
        tag.setFloat("debt",this.debt);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.debt = tag.getFloat("debt");
    }
}
