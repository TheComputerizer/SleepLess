package mods.thecomputerizer.sleepless.registry.entities.phantom;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
                this.entity.setMoveForward(0f);
                return;
            }
            float targetYaw = (float)(MathHelper.atan2(motionZ,motionX)*(180d/Math.PI))-90f;
            phantom.rotationYaw = this.limitAngle(this.entity.rotationYaw,targetYaw,180f);
            setEntityAISpeed(phantom);
            if(shouldJump(phantom)) {
                phantom.getJumpHelper().setJumping();
                setAction(Action.JUMPING);
            }
        }
        else if(this.action==Action.JUMPING) {
            setEntityAISpeed(phantom);
            if(phantom.onGround) setAction(Action.WAIT);
        }
    }

    private void setAction(Action action) {
        this.action = action;
    }

    private double getHorizontalOffset(boolean isX, double phantomX, double phantomZ) {
        return isX ? this.posX-phantomX : this.posZ-phantomZ;
    }

    private void setEntityAISpeed(P phantom) {
        double phantomSpeed = getEntitySpeed(phantom);
        BlockPos pos = getForwardPos(phantom,(float)phantomSpeed);
        if(isBlockPhantomPassable(phantom.getEntityWorld().getBlockState(pos).getBlock()))
            phantom.setAIMoveSpeed((float)(this.speed*phantomSpeed));
    }

    private boolean isBlockPhantomPassable(Block block) {
        for(Block blacklist : SleepLessConfigHelper.getPhantomPathfindBlacklist())
            if(block==blacklist) return false;
        return true;
    }

    private double getEntitySpeed(P phantom) {
        return phantom.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
    }

    private boolean shouldJump(P phantom) {
        double jumpHeight = EntityUtil.getTotalJumpHeight(phantom.getJumpMotion(),GRAVITY_FACTOR);
        AxisAlignedBB phantomBB = phantom.getEntityBoundingBox();
        double height = phantomBB.maxY-phantomBB.minY;
        double centerY = getCenterY(phantomBB);
        if(this.posY-centerY>height) {
            World world = phantom.getEntityWorld();
            BlockPos footPos = getVerticalPos(phantom.getEntityBoundingBox(),-0.02d);
            AxisAlignedBB footBB = world.getBlockState(footPos).getBoundingBox(world,footPos);
            return findNextSafeHeight(world,phantomBB,footBB,jumpHeight)>footBB.maxY+(phantomBB.maxY-centerY);
        }
        return false;
    }

    private double findNextSafeHeight(World world, AxisAlignedBB phantomBB, AxisAlignedBB footBB, double maxHeight) {
        double ret = footBB.maxY;
        AxisAlignedBB jumpBB = new AxisAlignedBB(phantomBB.minX,footBB.maxY,phantomBB.minZ,phantomBB.maxX,
                footBB.maxY+maxHeight,phantomBB.maxZ);
        for(BlockPos pos : EntityUtil.getSpecificBlockCollisions(world,jumpBB,
                SleepLessConfigHelper.getPhantomPathfindBlacklist())) {
            double y = pos.getY();
            if(y>ret) ret = y;
        }
        return ret;
    }

    private double getCenterY(AxisAlignedBB aabb) {
        return aabb.minY+((aabb.maxY-aabb.minY)/2d);
    }

    private BlockPos getForwardPos(P phantom, float forwardSpeed) {
        Vec3d storedMotion = getMotionVec(phantom);
        BlockPos footPos = getVerticalPos(phantom.getEntityBoundingBox(),-0.02d);
        phantom.moveRelative(0f,0f,forwardSpeed,getFriction(phantom,footPos));
        Vec3d relativeMotion = getMotionVec(phantom);
        setMotion(phantom,storedMotion);
        return new BlockPos(relativeMotion);
    }

    private Vec3d getMotionVec(P phantom) {
        return new Vec3d(phantom.motionX,phantom.motionY,phantom.motionZ);
    }

    private void setMotion(P phantom, Vec3d motionVec) {
        phantom.motionX = motionVec.x;
        phantom.motionY = motionVec.y;
        phantom.motionZ = motionVec.z;
    }

    private BlockPos getVerticalPos(AxisAlignedBB phantomBB, double offset) {
        int centerX = (int)(phantomBB.minX+((phantomBB.maxX-phantomBB.minX)/2d));
        int y = (int)(offset>=0d ? phantomBB.maxY+offset : phantomBB.minY-offset);
        int centerZ = (int)(phantomBB.minZ+((phantomBB.maxZ-phantomBB.minZ)/2d));
        return new BlockPos(centerX,y,centerZ);
    }

    private float getFriction(P phantom, BlockPos pos) {
        if(!phantom.onGround) return phantom.jumpMovementFactor;
        IBlockState state = phantom.getEntityWorld().getBlockState(pos);
        double slipperiness = state.getBlock().getSlipperiness(state,phantom.getEntityWorld(),pos,phantom)*0.91d;
        return (float)(0.16277136d/Math.pow(slipperiness,3));

    }
}
