package mods.thecomputerizer.sleepless.capability.sleepdebt;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mods.thecomputerizer.sleepless.capability.CapabilityHandler.SLEEP_DEBT_CAPABILITY;

@SuppressWarnings("ConstantConditions")
public class SleepDebtProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final ISleepDebt impl = SLEEP_DEBT_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability==SLEEP_DEBT_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == SLEEP_DEBT_CAPABILITY ? SLEEP_DEBT_CAPABILITY.cast(this.impl) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound)SLEEP_DEBT_CAPABILITY.getStorage().writeNBT(SLEEP_DEBT_CAPABILITY,this.impl,null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        SLEEP_DEBT_CAPABILITY.getStorage().readNBT(SLEEP_DEBT_CAPABILITY,this.impl,null,nbt);
    }
}
