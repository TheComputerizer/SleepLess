package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class PhaseTwo extends PhaseBase {


    public PhaseTwo(NightTerrorEntity entity, NBTTagCompound tag) {
        super(entity,tag);
    }

    public PhaseTwo(NightTerrorEntity entity, float minHealth) {
        super(entity,minHealth);
    }

    @Override
    protected PhaseAction makeActionQueue() {
        return new PhaseAction(PhaseAction.Type.SPAWN,150)
                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,90)
                        .setNextAction(new PhaseAction(PhaseAction.Type.TELEPORT,75)
                                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,40)
                                        .setNextAction(new PhaseAction(PhaseAction.Type.FLOAT,45)
                                                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,90))))));
    }

    @Override
    protected void onQueueFinished() {}

    @Override
    protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {
        PhaseBase nextPhase = new PhaseThree(this.entity,0f);
        nextPhase.inheretActionQueue(inheretedQueue);
        this.entity.setPhase(nextPhase);
    }
}
