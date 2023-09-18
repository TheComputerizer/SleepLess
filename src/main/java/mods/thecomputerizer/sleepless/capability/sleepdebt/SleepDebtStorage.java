package mods.thecomputerizer.sleepless.capability.sleepdebt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SleepDebtStorage implements Capability.IStorage<ISleepDebt> {

    @Override
    public @Nullable NBTBase writeNBT(Capability<ISleepDebt> capability, ISleepDebt instance, EnumFacing side) {
        return instance.writeToNBT();
    }

    @Override
    public void readNBT(Capability<ISleepDebt> capability, ISleepDebt instance, EnumFacing side, NBTBase nbt) {
        if(nbt instanceof NBTTagCompound) instance.readFromNBT((NBTTagCompound)nbt);
    }
}
