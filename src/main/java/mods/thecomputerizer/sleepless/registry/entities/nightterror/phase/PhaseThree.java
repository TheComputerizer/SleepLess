package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.FLOAT;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.SPAWN;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.TELEPORT;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseAction.Type.WAIT;

public class PhaseThree extends PhaseBase {

    public PhaseThree(NightTerrorEntity entity, NBTTagCompound tag) {
        super(entity,tag);
    }

    public PhaseThree(NightTerrorEntity entity, float minHealth) {
        super(entity,minHealth);
    }

    @Override protected PhaseAction makeActionQueue() {
        return SPAWN.create(100)
                .setNextAction(WAIT.create(75)
                        .setNextAction(TELEPORT.create(50)
                                .setNextAction(WAIT.create(25)
                                        .setNextAction(FLOAT.create(40)
                                                .setNextAction(WAIT.create(75))))));
    }

    @Override protected void onQueueFinished() {}

    @Override protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {}

    @Override public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = super.writeToNBT();
        tag.setInteger("phaseNumber",3);
        return tag;
    }
}