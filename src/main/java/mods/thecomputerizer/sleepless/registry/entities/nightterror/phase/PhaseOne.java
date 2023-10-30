package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class PhaseOne extends PhaseBase {

    public PhaseOne(NightTerrorEntity entity, NBTTagCompound tag) {
        super(entity,tag);
    }

    public PhaseOne(NightTerrorEntity entity, float minHealth) {
        super(entity,minHealth);
    }

    @Override
    protected PhaseAction makeActionQueue() {
        return new PhaseAction(PhaseAction.Type.SPAWN,200)
                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,100)
                        .setNextAction(new PhaseAction(PhaseAction.Type.TELEPORT,100)
                                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,50))));
    }

    @Override
    protected void onQueueFinished() {}

    @Override
    protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {
        PhaseBase nextPhase = new PhaseTwo(this.entity,0.25f);
        nextPhase.inheretActionQueue(inheretedQueue);
        this.entity.setPhase(nextPhase);
    }
}
