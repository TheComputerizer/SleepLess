package mods.thecomputerizer.sleepless.capability.sleepdebt;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface ISleepDebt {

    boolean onTicksSlept(long ticks);
    float getDebt();
    void sync(EntityPlayerMP player);
    NBTTagCompound writeToNBT();
    void readFromNBT(NBTTagCompound tag);
}
