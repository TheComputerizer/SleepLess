package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;


import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class PhaseBase {

    protected final NightTerrorEntity entity;
    protected final float minHealth;
    protected PhaseAction currentAction;

    protected PhaseBase(NightTerrorEntity entity, NBTTagCompound tag) {
        this.entity = entity;
        this.minHealth = tag.getFloat("minHealth");
        if(tag.hasKey("actionQueue"))
            this.currentAction = new PhaseAction(tag.getCompoundTag("actionQueue"));
    }

    protected PhaseBase(NightTerrorEntity entity, float minHealth) {
        this.entity = entity;
        this.minHealth = minHealth;
    }

    public void onTick() {
        if(this.entity.isNotDying() && !this.entity.isDead) {
            if(Objects.isNull(this.currentAction)) this.currentAction = makeActionQueue();
            else {
                PhaseAction nextAction = this.currentAction.getTickedAction(this.entity);
                if(Objects.isNull(nextAction)) {
                    onQueueFinished();
                    nextAction = makeActionQueue();
                }
                this.currentAction = nextAction;
            }
        }
    }

    protected abstract PhaseAction makeActionQueue();

    protected abstract void onQueueFinished();

    protected abstract void setNextPhase(@Nullable PhaseAction inheretedQueue);

    protected void inheretActionQueue(@Nullable PhaseAction inheretedQueue) {
        PhaseAction queue = makeActionQueue();
        this.currentAction = Objects.nonNull(inheretedQueue) ? inheretedQueue.setNextAction(queue) : queue;
    }

    public boolean canDie() {
        return this.minHealth<=0f;
    }

    public void onDamage() {
        if(this.entity.isNotDying() && !this.entity.isDead) {
            this.currentAction = makeDamageAction(this.currentAction.getNextAction());
            float dmgPercent = this.entity.getHealth()/this.entity.getMaxHealth();
            if(dmgPercent<=this.minHealth) {
                this.entity.setHealth(this.entity.getMaxHealth()*this.minHealth);
                setNextPhase(this.currentAction);
            }
        }
    }

    private PhaseAction makeDamageAction(@Nullable PhaseAction nextAction) {
        return PhaseAction.Type.DAMAGE.create(15)
                .setNextAction(PhaseAction.Type.TELEPORT.create(100)
                        .setNextAction(PhaseAction.Type.WAIT.create(50)
                                .setNextAction(nextAction)));
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setFloat("minHealth",this.minHealth);
        if(Objects.nonNull(this.currentAction)) tag.setTag("actionQueue",this.currentAction.writeToNBT());
        return tag;
    }
}
