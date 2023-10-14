package mods.thecomputerizer.sleepless.capability.nightterror;

import mods.thecomputerizer.sleepless.world.nightterror.NightTerror;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;

public interface INightTerrorCap {

    void checkInstance(WorldServer world);
    void setInstance(NightTerror instance);
    NightTerror getInstance();
    boolean shoudlDaylightCycle();
    void onPlayerJoinWorld(EntityPlayerMP player);
    void finish();
    NBTTagCompound writeToNBT();
    void readFromNBT(NBTTagCompound tag);
}
