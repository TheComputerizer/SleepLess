package mods.thecomputerizer.sleepless.registry.entities.phantom;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.util.EntityUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@SuppressWarnings("unchecked")
public class PhantomMoveHelper<P extends PhantomEntity> extends EntityMoveHelper {

    private static final double GRAVITY_FACTOR = 0.20000000298023224d;

    public PhantomMoveHelper(P phantom) {
        super(phantom);
    }

    public P getPhantomEntity() {
        return (P)this.entity;
    }

    @Override
    public void onUpdateMoveHelper() {
        P phantom = getPhantomEntity();
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

    private void setAction(Action action) {
        this.action = action;
    }

    private double getHorizontalOffset(boolean isX, double phantomX, double phantomZ) {
        return isX ? this.posX-phantomX : this.posZ-phantomZ;
    }

    private void setEntityAISpeed(P phantom) {
        double phantomSpeed = getEntitySpeed(phantom);
        phantom.setAIMoveSpeed((float)(this.speed*phantomSpeed));
    }

    private double getEntitySpeed(P phantom) {
        return phantom.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
    }

    private boolean shouldJump(P phantom) {
        double jumpHeight = EntityUtil.getTotalJumpHeight(phantom.getJumpMotion(),GRAVITY_FACTOR);
        AxisAlignedBB phantomBB = phantom.getEntityBoundingBox();
        if(this.posY>phantomBB.maxY) {
            World world = phantom.getEntityWorld();
            BlockPos footPos = getFootPos(phantom.getEntityBoundingBox());
            AxisAlignedBB footBB = world.getBlockState(footPos).getBoundingBox(world,footPos);
            return findNextSafeHeight(world,phantomBB,footBB,jumpHeight)>footBB.maxY+(phantomBB.maxY-getCenterY(phantomBB));
        }
        return false;
    }

    private double findNextSafeHeight(World world, AxisAlignedBB phantomBB, AxisAlignedBB footBB, double maxHeight) {
        double ret = footBB.maxY;
        AxisAlignedBB jumpBB = new AxisAlignedBB(phantomBB.minX,footBB.maxY,phantomBB.minZ,phantomBB.maxX,
                footBB.maxY+maxHeight,phantomBB.maxZ);
        for(AxisAlignedBB aabb : EntityUtil.getSpecificBlockCollisions(world,jumpBB,
                SleepLessConfigHelper.getPhantomPathfindBlacklist())) {
            double y = aabb.minY;
            if(y>ret) ret = y;
        }
        return ret;
    }

    private double getCenterY(AxisAlignedBB aabb) {
        return aabb.minY+((aabb.maxY-aabb.minY)/2d);
    }

    private BlockPos getFootPos(AxisAlignedBB phantomBB) {
        int centerX = (int)(phantomBB.minX+((phantomBB.maxX-phantomBB.minX)/2d));
        int y = (int)(phantomBB.minY-0.02);
        int centerZ = (int)(phantomBB.minZ+((phantomBB.maxZ-phantomBB.minZ)/2d));
        return new BlockPos(centerX,y,centerZ);
    }
}
