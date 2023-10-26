package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomEntity;
import mods.thecomputerizer.sleepless.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public boolean noClip;

    @Shadow public World world;

    @Shadow private long pistonDeltasGameTime;

    @Shadow @Final private double[] pistonDeltas;

    @Shadow public double posX;

    @Shadow public double posY;

    @Shadow public double posZ;

    @Shadow protected boolean isInWeb;

    @Shadow public double motionX;

    @Shadow public double motionY;

    @Shadow public double motionZ;

    @Shadow public boolean onGround;

    @Shadow public abstract boolean isSneaking();

    @Shadow public abstract AxisAlignedBB getEntityBoundingBox();

    @Shadow public abstract void setEntityBoundingBox(AxisAlignedBB bb);

    @Shadow public abstract void resetPositionToBB();

    @Shadow public boolean collidedHorizontally;

    @Shadow public boolean collidedVertically;

    @Shadow public boolean collided;

    @Shadow protected abstract void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos);

    @Shadow protected abstract boolean canTriggerWalking();

    @Shadow public abstract boolean isRiding();

    @Shadow public abstract void addEntityCrashInfo(CrashReportCategory category);

    @Shadow public abstract boolean isBurning();

    @Shadow public abstract void playSound(SoundEvent soundIn, float volume, float pitch);

    @Shadow protected Random rand;

    @Shadow private int fire;

    @Shadow protected abstract int getFireImmuneTicks();

    @Shadow public abstract void setFire(int seconds);

    @Shadow public abstract boolean isWet();

    @Shadow protected abstract void dealFireDamage(int amount);

    @Shadow protected abstract void doBlockCollisions();

    @Shadow private float nextFlap;

    @Shadow protected abstract float playFlySound(float p_191954_1_);

    @Shadow public float distanceWalkedOnStepModified;

    @Shadow protected abstract boolean makeFlySound();

    @Shadow public float distanceWalkedModified;

    @Shadow private int nextStepDistance;

    @Shadow public abstract boolean isInWater();

    @Shadow public abstract boolean isBeingRidden();

    @Shadow @Nullable public abstract Entity getControllingPassenger();

    @Shadow protected abstract SoundEvent getSwimSound();

    @Shadow protected abstract void playStepSound(BlockPos pos, Block blockIn);

    @Unique private Entity sleepless$cast() {
        return (Entity)(Object)this;
    }

    @Unique private boolean sleepless$hasPhasedEffect() {
        Entity entity = sleepless$cast();
        if(!(entity instanceof EntityLivingBase)) return false;
        return ((EntityLivingBase)entity).isPotionActive(PotionRegistry.PHASED);
    }
    
    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    private void sleepless$move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        if(!this.noClip && sleepless$hasPhasedEffect()) {
            sleepless$phasedMovement(type,x,y,z);
            ci.cancel();
        }
    }

    @Unique private void sleepless$phasedMovement(MoverType type, double x, double y, double z) {
        if(type == MoverType.PISTON) {
            long time = this.world.getTotalWorldTime();
            if(time != this.pistonDeltasGameTime) {
                Arrays.fill(this.pistonDeltas,0d);
                this.pistonDeltasGameTime = time;
            }
            if(x != 0d) {
                int j = EnumFacing.Axis.X.ordinal();
                double d0 = MathHelper.clamp(x+this.pistonDeltas[j],-0.51d,0.51d);
                x = d0 - this.pistonDeltas[j];
                this.pistonDeltas[j] = d0;
                if(Math.abs(x) <= 9.999999747378752E-6d) return;
            }
            else if (y != 0d) {
                int l4 = EnumFacing.Axis.Y.ordinal();
                double d12 = MathHelper.clamp(y+this.pistonDeltas[l4],-0.51d,0.51d);
                y = d12 - this.pistonDeltas[l4];
                this.pistonDeltas[l4] = d12;
                if (Math.abs(y) <= 9.999999747378752E-6d) return;
            }
            else {
                if(z == 0d) return;
                int i5 = EnumFacing.Axis.Z.ordinal();
                double d13 = MathHelper.clamp(z+this.pistonDeltas[i5],-0.51d,0.51d);
                z = d13 - this.pistonDeltas[i5];
                this.pistonDeltas[i5] = d13;
                if(Math.abs(z) <= 9.999999747378752E-6d) return;
            }
        }
        this.world.profiler.startSection("move");
        double storedPosX = this.posX;
        double storedPosY = this.posY;
        double storedPosZ = this.posZ;
        if(this.isInWeb) {
            this.isInWeb = false;
            y *= 0.05000000074505806d;
            this.motionX = 0d;
            this.motionY = 0d;
            this.motionZ = 0d;
        }
        double storedX = x;
        double storedY = y;
        double storedZ = z;
        Entity self = sleepless$cast();
        boolean normalGround = y<0d && !self.isSneaking();
        if(normalGround) {
            List<AxisAlignedBB> normalCollisions = this.world.getCollisionBoxes(self,this.getEntityBoundingBox().expand(0,y,0));
            for(AxisAlignedBB aabb : normalCollisions)
                y = aabb.calculateYOffset(this.getEntityBoundingBox(),y);
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0d,y,0d));
        }
        List<AxisAlignedBB> phantomCollisions = EntityUtil.getSpecificBlockCollisions(this.world,
                this.getEntityBoundingBox().offset(x,normalGround ? 0d : y,z),self instanceof PhantomEntity ?
                        SleepLessConfigHelper.getPhantomPathfindBlacklist() : SleepLessConfigHelper.getPhasedBlockBlacklist());
        if(x!=0d) {
            for(AxisAlignedBB aabb : phantomCollisions)
                x = aabb.calculateXOffset(this.getEntityBoundingBox(),x);
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x,0d,0d));
        }
        if(y>0d || (y<0d && self.isSneaking())) {
            for(AxisAlignedBB aabb : phantomCollisions)
                y = aabb.calculateYOffset(this.getEntityBoundingBox(),y);
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0d,y,0d));
        }
        if(z!=0d) {
            for(AxisAlignedBB aabb : phantomCollisions)
                z = aabb.calculateZOffset(this.getEntityBoundingBox(),z);
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0d,0d,z));
        }
        this.world.profiler.endSection();
        this.world.profiler.startSection("rest");
        this.resetPositionToBB();
        this.collidedHorizontally = false;
        this.collidedVertically = storedY != y;
        this.onGround = this.collidedVertically && storedY<0d;
        this.collided = this.collidedVertically;
        int flooredPosX = MathHelper.floor(this.posX);
        int flooredPosY = MathHelper.floor(this.posY-0.20000000298023224d);
        int flooredPosZ = MathHelper.floor(this.posZ);
        BlockPos pos = new BlockPos(flooredPosX,flooredPosY,flooredPosZ);
        IBlockState state = this.world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR) {
            BlockPos pos1 = pos.down();
            IBlockState state1 = this.world.getBlockState(pos1);
            Block block1 = state1.getBlock();
            if(block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate) {
                state = state1;
                pos = pos1;
            }
        }
        this.updateFallState(y,this.onGround,state,pos);
        if(storedX!=x) this.motionX = 0d;
        if(storedZ!=z) this.motionZ = 0d;
        Block block = state.getBlock();
        if(storedY!=y) block.onLanded(this.world,self);
        if(this.canTriggerWalking() && (!this.onGround || !this.isSneaking() || !(self instanceof EntityPlayer)) && !this.isRiding()) {
            double offsetX = this.posX-storedPosX;
            double offsetY = this.posY-storedPosY;
            double offsetZ = this.posZ-storedPosZ;
            if(block != Blocks.LADDER) offsetY = 0d;
            if(block != Blocks.AIR && this.onGround) block.onEntityWalk(this.world,pos,self);
            this.distanceWalkedModified = (float)((double)this.distanceWalkedModified+(double)MathHelper.sqrt(offsetX*offsetX + offsetZ*offsetZ)*0.6d);
            this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified+(double)MathHelper.sqrt(offsetX*offsetX + offsetY*offsetY + offsetZ*offsetZ)*0.6d);
            if(this.distanceWalkedOnStepModified > (float)this.nextStepDistance && state.getMaterial() != Material.AIR) {
                this.nextStepDistance = (int)this.distanceWalkedOnStepModified+1;
                if(this.isInWater()) {
                    Entity entity = this.isBeingRidden() && Objects.nonNull(this.getControllingPassenger()) ? this.getControllingPassenger() : self;
                    float f = entity == self ? 0.35f : 0.4f;
                    float f1 = MathHelper.sqrt(entity.motionX*entity.motionX*0.20000000298023224d + entity.motionY*entity.motionY + entity.motionZ*entity.motionZ*0.20000000298023224d)*f;
                    if(f1>1f) f1 = 1f;
                    this.playSound(this.getSwimSound(),f1,1f +(this.rand.nextFloat()-this.rand.nextFloat())*0.4f);
                }
                else this.playStepSound(pos,block);
            }
            else if (this.distanceWalkedOnStepModified>this.nextFlap && this.makeFlySound() && state.getMaterial()==Material.AIR)
                this.nextFlap = this.playFlySound(this.distanceWalkedOnStepModified);
        }
        try {
            this.doBlockCollisions();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
        boolean wetFlag = this.isWet();
        if(this.world.isFlammableWithin(this.getEntityBoundingBox().shrink(0.001d))) {
            this.dealFireDamage(1);
            if(!wetFlag) {
                ++this.fire;
                if (this.fire == 0) this.setFire(8);
            }
        }
        else if(this.fire<=0) this.fire -= this.getFireImmuneTicks();
        if (wetFlag && this.isBurning()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,0.7f,1.6f+(this.rand.nextFloat()-this.rand.nextFloat())*0.4f);
            this.fire -= this.getFireImmuneTicks();
        }
        this.world.profiler.endSection();
    }

    @Inject(at = @At("HEAD"), method = "pushOutOfBlocks", cancellable = true)
    private void sleepless$pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if(sleepless$hasPhasedEffect()) cir.setReturnValue(false);
    }
}
