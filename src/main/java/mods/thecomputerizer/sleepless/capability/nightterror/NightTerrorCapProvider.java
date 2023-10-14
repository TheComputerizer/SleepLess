package mods.thecomputerizer.sleepless.capability.nightterror;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mods.thecomputerizer.sleepless.capability.CapabilityHandler.NIGHT_TERROR_CAPABILITY;

@SuppressWarnings("ConstantConditions")
public class NightTerrorCapProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final INightTerrorCap impl = NIGHT_TERROR_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability==NIGHT_TERROR_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == NIGHT_TERROR_CAPABILITY ? NIGHT_TERROR_CAPABILITY.cast(this.impl) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound)NIGHT_TERROR_CAPABILITY.getStorage().writeNBT(NIGHT_TERROR_CAPABILITY,this.impl,null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NIGHT_TERROR_CAPABILITY.getStorage().readNBT(NIGHT_TERROR_CAPABILITY,this.impl,null,nbt);
    }
}