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
    }

    protected PhaseBase(NightTerrorEntity entity, float minHealth) {
        this.entity = entity;
        this.minHealth = minHealth;
    }

    public void onTick() {
        if(this.entity.isNotDying() && !this.entity.isDead) {
            if(Objects.isNull(this.currentAction)) {
                this.currentAction = makeActionQueue();
                this.currentAction.onStart(this.entity);
            }
            else {
                PhaseAction nextAction = this.currentAction.getTickedAction();
                boolean madeNewQueue = false;
                if(Objects.isNull(nextAction)) {
                    onQueueFinished();
                    nextAction = makeActionQueue();
                    madeNewQueue = true;
                }
                if(madeNewQueue || nextAction != this.currentAction) nextAction.onStart(this.entity);
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
            this.currentAction.onStart(this.entity);
            float dmgPercent = this.entity.getHealth()/this.entity.getMaxHealth();
            if(dmgPercent<=this.minHealth) {
                this.entity.setHealth(this.entity.getMaxHealth()*this.minHealth);
                setNextPhase(this.currentAction);
            }
        }
    }

    private PhaseAction makeDamageAction(@Nullable PhaseAction nextAction) {
        return new PhaseAction(PhaseAction.Type.DAMAGE,15)
                .setNextAction(new PhaseAction(PhaseAction.Type.TELEPORT,100)
                        .setNextAction(new PhaseAction(PhaseAction.Type.WAIT,50)
                                .setNextAction(nextAction)));
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setFloat("minHealth",this.minHealth);
        return tag;
    }
}
