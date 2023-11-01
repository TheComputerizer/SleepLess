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
        return PhaseAction.Type.SPAWN.create(100)
                .setNextAction(PhaseAction.Type.WAIT.create(75)
                        .setNextAction(PhaseAction.Type.TELEPORT.create(50)
                                .setNextAction(PhaseAction.Type.WAIT.create(25)
                                        .setNextAction(PhaseAction.Type.FLOAT.create(40)
                                                .setNextAction(PhaseAction.Type.WAIT.create(75))))));
    }

    @Override
    protected void onQueueFinished() {}

    @Override
    protected void setNextPhase(@Nullable PhaseAction inheretedQueue) {}

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = super.writeToNBT();
        tag.setInteger("phaseNumber",3);
        return tag;
    }
}
