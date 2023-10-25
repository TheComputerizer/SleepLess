package mods.thecomputerizer.sleepless.registry.entities.ai;

import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class PhantomAttackMelee<P extends PhantomEntity> extends EntityAIBase {

    private final P phantom;
    private final double speed;
    private final boolean ignoreSight;
    private int delayCounter;
    private int attackTick;
    private Vec3d targetVec = Vec3d.ZERO;

    public PhantomAttackMelee(P phantom, double speed, boolean ignoreSight) {
        this.phantom = phantom;
        this.speed = speed;
        this.ignoreSight = ignoreSight;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entity = this.phantom.getAttackTarget();
        return Objects.nonNull(entity) && entity.isEntityAlive();
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase entity = this.phantom.getAttackTarget();
        if(Objects.isNull(entity) || !entity.isEntityAlive() || !this.phantom.isWithinHomeDistanceFromPosition(entity.getPosition()))
            return false;
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            return !player.isSpectator() && !player.isCreative();
        }
        return true;
    }

    @Override
    public void startExecuting() {
        EntityLivingBase entity = this.phantom.getAttackTarget();
        if(Objects.nonNull(entity)) {
            this.phantom.getMoveHelper().setMoveTo(entity.posX,entity.posY,entity.posZ,this.speed);
            this.delayCounter = 0;
        }
    }

    @Override
    public void resetTask() {
        EntityLivingBase entity = this.phantom.getAttackTarget();
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if(player.isSpectator() || player.isCreative()) this.phantom.setAttackTarget(null);
        }
        this.phantom.getMoveHelper().action = EntityMoveHelper.Action.WAIT;
    }

    @Override
    public void updateTask() {
        EntityLivingBase entity = this.phantom.getAttackTarget();
        if(Objects.nonNull(entity)) {
            this.phantom.getLookHelper().setLookPositionWithEntity(entity, 30.0F, 30.0F);
            double distanceSq = this.phantom.getDistanceSq(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
            this.delayCounter--;
            if((this.ignoreSight || this.phantom.getEntitySenses().canSee(entity)) && this.delayCounter<=0 &&
                    (this.targetVec.equals(Vec3d.ZERO) || getEntityDistSqr(entity,this.targetVec)>=1d ||
                            this.phantom.getRNG().nextFloat()<0.05f)) {
                this.targetVec = new Vec3d(entity.posX,entity.getEntityBoundingBox().minY,entity.posZ);
                this.delayCounter = 4+this.phantom.getRNG().nextInt(7);
                if(distanceSq>1024d) this.delayCounter+=10;
                else if(distanceSq>256d) this.delayCounter+=5;
                this.phantom.getMoveHelper().setMoveTo(entity.posX,entity.posY,entity.posZ,this.speed);
            }
            this.attackTick = Math.max(this.attackTick-1,0);
            this.checkAndPerformAttack(entity,distanceSq);
        }
    }

    protected void checkAndPerformAttack(EntityLivingBase target, double distToTargetSqr) {
        double reachSqr = this.attackReachSqr(target);
        if(distToTargetSqr<=reachSqr && this.attackTick<=0) {
            this.attackTick = 20;
            this.phantom.swingArm(EnumHand.MAIN_HAND);
            this.phantom.attackEntityAsMob(target);
        }
    }

    protected double attackReachSqr(EntityLivingBase target) {
        return this.phantom.width*2f*this.phantom.width*2f+target.width;
    }

    protected double getEntityDistSqr(EntityLivingBase entity, Vec3d vec) {
        return entity.getDistanceSq(vec.x,vec.y,vec.z);
    }
}
