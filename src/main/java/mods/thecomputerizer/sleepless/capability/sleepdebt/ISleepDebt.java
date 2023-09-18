package mods.thecomputerizer.sleepless.capability.sleepdebt;

import net.minecraft.nbt.NBTTagCompound;

public interface ISleepDebt {

    void onTicksSlept(long ticks);

    float getDebt();
    NBTTagCompound writeToNBT();
    void readFromNBT(NBTTagCompound tag);
}
