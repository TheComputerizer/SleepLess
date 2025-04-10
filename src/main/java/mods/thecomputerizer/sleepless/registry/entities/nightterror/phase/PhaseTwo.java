package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.FLOAT;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.SPAWN;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.TELEPORT;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.WAIT;

public class PhaseTwo extends PhaseBase {
    
    public PhaseTwo(NightTerrorEntity entity, NBTTagCompound tag) {
        super(entity,tag);
    }

    public PhaseTwo(NightTerrorEntity entity, float minHealth) {
        super(entity,minHealth);
    }

    @Override protected PhaseAction makeActionQueue() {
        return SPAWN.create(150)
                .setNextAction(WAIT.create(90)
                        .setNextAction(TELEPORT.create(75)
                                .setNextAction(WAIT.create(40)
                                        .setNextAction(FLOAT.create(45)
                                                .setNextAction(WAIT.create(90))))));
    }

    @Override protected void onQueueFinished() {}

    @Override protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {
        PhaseBase nextPhase = new PhaseThree(this.entity,0f);
        nextPhase.inheretActionQueue(inheretedQueue);
        this.entity.setPhase(nextPhase);
    }

    @Override public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = super.writeToNBT();
        tag.setInteger("phaseNumber",2);
        return tag;
    }
}