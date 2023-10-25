package mods.thecomputerizer.sleepless.capability.nightterror;

import mods.thecomputerizer.sleepless.SleepLess;
import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerror;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NightTerrorCap implements INightTerrorCap {

    private NightTerror instance;
    private int cooldown;

    @Override
    public void checkInstance(WorldServer world) {
        long time = world.getWorldTime()%24000L;
        if(time<13000L) CapabilityHandler.finishNightTerror(world);
        else {
            if(this.cooldown>0) this.cooldown--;
            if(Objects.isNull(this.instance) && SleepLess.fudgeInt(this.cooldown,0)<=0 && time<16000L) {
                this.cooldown = 300;
                List<Float> chances = new ArrayList<>();
                for(EntityPlayer player : world.playerEntities) {
                    float chance = SleepLessConfigHelper.nightTerrorChance((EntityPlayerMP)player);
                    if(chance>0) chances.add(chance);
                }
                if(!chances.isEmpty() && SleepLess.fudgeFloat(world.rand.nextFloat(),0f)<SleepLessConfigHelper.calculateFinalChance(chances))
                    setInstance(new NightTerror(world));
            }
        }
    }

    @Override
    public void setInstance(NightTerror instance) {
        this.instance = instance;
    }

    @Override
    public NightTerror getInstance() {
        return this.instance;
    }

    @Override
    public boolean shoudlDaylightCycle() {
        return Objects.isNull(this.instance) || this.instance.shoudlDaylightCycle();
    }

    @Override
    public void onPlayerJoinWorld(EntityPlayerMP player) {
        if(Objects.nonNull(this.instance)) this.instance.catchUpJoiningPlayer(player);
    }

    @Override
    public void finish() {
        if(Objects.nonNull(this.instance)) {
            this.instance.finish();
            this.instance = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("cooldownTime",this.cooldown);
        return Objects.nonNull(this.instance) ? this.instance.writeToNBT(tag) : tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.cooldown = tag.getInteger("cooldownTime");
        if(tag.hasKey("instance")) this.instance = new NightTerror(tag.getCompoundTag("instance"));
    }
}
