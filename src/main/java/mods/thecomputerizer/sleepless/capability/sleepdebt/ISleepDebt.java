package mods.thecomputerizer.sleepless.capability.sleepdebt;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface ISleepDebt {

    void of(EntityPlayerMP player, SleepDebt cap);
    boolean onTicksSlept(long ticks);
    float getDebt();
    void setDebt(EntityPlayerMP player, float debt);
    float getHungerAmplifier();
    float getMiningSpeedFactor();
    float getPhantomFactor();
    void sync(EntityPlayerMP player);
    NBTTagCompound writeToNBT();
    void readFromNBT(NBTTagCompound tag);
}
