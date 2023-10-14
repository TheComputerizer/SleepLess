package mods.thecomputerizer.sleepless.capability.nightterror;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class NightTerrorCapStorage implements Capability.IStorage<INightTerrorCap> {

    @Override
    public @Nullable NBTBase writeNBT(Capability<INightTerrorCap> capability, INightTerrorCap instance, EnumFacing side) {
        return instance.writeToNBT();
    }

    @Override
    public void readNBT(Capability<INightTerrorCap> capability, INightTerrorCap instance, EnumFacing side, NBTBase nbt) {
        if(nbt instanceof NBTTagCompound) instance.readFromNBT((NBTTagCompound)nbt);
    }
}