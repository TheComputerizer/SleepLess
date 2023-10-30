package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class PhaseSpawn extends PhaseBase {

    public PhaseSpawn(NightTerrorEntity entity, NBTTagCompound tag) {
        super(entity,tag);
    }

    public PhaseSpawn(NightTerrorEntity entity, float minHealth) {
        super(entity,minHealth);
    }

    @Override
    protected PhaseAction makeActionQueue() {
        return new PhaseAction(PhaseAction.Type.TRANSITION,200)
                .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,100).setInvertInvulnerability());
    }

    @Override
    protected void onQueueFinished() {
        this.entity.setPhase(new PhaseOne(this.entity,0.5f));
    }

    @Override
    protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {
        PhaseBase nextPhase = new PhaseOne(this.entity,0.5f);
        nextPhase.inheretActionQueue(inheretedQueue);
        this.entity.setPhase(nextPhase);
    }
}
