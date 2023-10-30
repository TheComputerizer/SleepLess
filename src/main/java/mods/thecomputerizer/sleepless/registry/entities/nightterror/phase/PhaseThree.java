package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class PhaseThree extends PhaseBase {

    public PhaseThree(NightTerrorEntity entity, NBTTagCompound tag) {
        super(entity,tag);
    }

    public PhaseThree(NightTerrorEntity entity, float minHealth) {
        super(entity,minHealth);
    }

    @Override
    protected PhaseAction makeActionQueue() {
        return new PhaseAction(PhaseAction.Type.SPAWN,100)
                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,75)
                        .setNextAction(new PhaseAction(PhaseAction.Type.TELEPORT,50)
                                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,25)
                                        .setNextAction(new PhaseAction(PhaseAction.Type.FLOAT,100)
                                                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,75))))));
    }

    @Override
    protected void onQueueFinished() {}

    @Override
    protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {}
}
