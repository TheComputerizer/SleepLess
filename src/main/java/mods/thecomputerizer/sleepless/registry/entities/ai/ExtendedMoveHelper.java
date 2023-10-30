package mods.thecomputerizer.sleepless.registry.entities.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("unchecked")
public abstract class ExtendedMoveHelper<E extends EntityLiving> extends EntityMoveHelper {

    protected ExtendedMoveHelper(E entity) {
        super(entity);
    }

    protected E getEntity() {
        return (E)this.entity;
    }

    protected void setAction(Action action) {
        this.action = action;
    }

    protected double getHorizontalOffset(boolean isX, double phantomX, double phantomZ) {
        return isX ? this.posX-phantomX : this.posZ-phantomZ;
    }

    protected void setEntityAISpeed(E entity) {
        double phantomSpeed = getEntitySpeed(entity);
        entity.setAIMoveSpeed((float)(this.speed*phantomSpeed));
    }

    protected double getEntitySpeed(E entity) {
        return entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
    }

    protected BlockPos getFootPos(AxisAlignedBB entityBB) {
        int centerX = (int)(entityBB.minX+((entityBB.maxX-entityBB.minX)/2d));
        int y = (int)(entityBB.minY-0.02d);
        int centerZ = (int)(entityBB.minZ+((entityBB.maxZ-entityBB.minZ)/2d));
        return new BlockPos(centerX,y,centerZ);
    }

    protected void setEntityMotion(Vec3d motionVec, boolean isAdd) {
        setEntityMotion(motionVec.x,motionVec.y,motionVec.z,isAdd);
    }

    protected void setEntityMotion(double x, double y, double z, boolean isAdd) {
        E entity = getEntity();
        entity.motionX = isAdd ? entity.motionX+x : x;
        entity.motionY = isAdd ? entity.motionY+y : y;
        entity.motionZ = isAdd ? entity.motionZ+z : z;
    }
}
