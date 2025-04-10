package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.SPAWN;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.TELEPORT;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.WAIT;

public class PhaseOne extends PhaseBase {

    public PhaseOne(NightTerrorEntity entity, NBTTagCompound tag) {
        super(entity,tag);
    }

    public PhaseOne(NightTerrorEntity entity, float minHealth) {
        super(entity,minHealth);
    }

    @Override protected PhaseAction makeActionQueue() {
        return SPAWN.create(200)
                .setNextAction(WAIT.create(100)
                        .setNextAction(TELEPORT.create(100)
                                .setNextAction(WAIT.create(50))));
    }

    @Override protected void onQueueFinished() {}

    @Override protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {
        PhaseBase nextPhase = new PhaseTwo(this.entity,0.25f);
        nextPhase.inheretActionQueue(inheretedQueue);
        this.entity.setPhase(nextPhase);
    }

    @Override public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = super.writeToNBT();
        tag.setInteger("phaseNumber",1);
        return tag;
    }
}