package mods.thecomputerizer.sleepless.registry.entities.phantom;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.registry.entities.ai.ExtendedMoveHelper;
import mods.thecomputerizer.sleepless.util.EntityUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class PhantomMoveHelper<P extends PhantomEntity> extends ExtendedMoveHelper<P> {

    private static final double GRAVITY_FACTOR = 0.20000000298023224d;

    public PhantomMoveHelper(P phantom) {
        super(phantom);
    }

    @Override
    public void onUpdateMoveHelper() {
        P phantom = getEntity();
        if(this.action==Action.MOVE_TO) {
            setAction(Action.WAIT);
            double motionX = getHorizontalOffset(true,phantom.posX,phantom.posZ);
            double motionZ = getHorizontalOffset(false,phantom.posX,phantom.posZ);
            if(Math.pow(motionX,2)+Math.pow(this.posY-phantom.posY,2)+Math.pow(motionZ,2)<2.500000277905201E-7d) {
                phantom.setMoveForward(0f);
                return;
            }
            float targetYaw = (float)(MathHelper.atan2(motionZ,motionX)*(180d/Math.PI))-90f;
            phantom.rotationYaw = this.limitAngle(this.entity.rotationYaw,targetYaw,180f);
            setEntityAISpeed(phantom);
            phantom.setSneaking(this.posY<phantom.getEntityBoundingBox().minY);
            if(shouldJump(phantom)) {
                phantom.getJumpHelper().setJumping();
                setAction(Action.JUMPING);
            }
        }
        else if(this.action==Action.JUMPING) {
            phantom.setSneaking(false);
            setEntityAISpeed(phantom);
            if(phantom.onGround) setAction(Action.WAIT);
        } else phantom.setSneaking(false);
    }

    private boolean shouldJump(P phantom) {
        double jumpHeight = EntityUtil.getTotalJumpHeight(phantom.getJumpMotion(),GRAVITY_FACTOR);
        AxisAlignedBB phantomBB = phantom.getEntityBoundingBox();
        if(this.posY>phantomBB.maxY) {
            World world = phantom.getEntityWorld();
            BlockPos footPos = getFootPos(phantom.getEntityBoundingBox());
            AxisAlignedBB footBB = world.getBlockState(footPos).getBoundingBox(world,footPos);
            AxisAlignedBB jumpBB = new AxisAlignedBB(phantomBB.minX,footBB.maxY,phantomBB.minZ,phantomBB.maxX,
                    footBB.maxY+jumpHeight,phantomBB.maxZ);
            List<AxisAlignedBB> collisionList = EntityUtil.getSpecificBlockCollisions(world,jumpBB,
                    SleepLessConfigHelper.getPhantomPathfindBlacklist());
            if(collisionList.isEmpty()) return true;
            return findNextSafeHeight(collisionList,jumpBB.maxY)>phantomBB.maxY+1d;
        }
        return false;
    }

    private double findNextSafeHeight(List<AxisAlignedBB> collisionList, double height) {
        for(AxisAlignedBB aabb : collisionList)
            if(aabb.minY<height) height = aabb.minY;
        return height;
    }
}
