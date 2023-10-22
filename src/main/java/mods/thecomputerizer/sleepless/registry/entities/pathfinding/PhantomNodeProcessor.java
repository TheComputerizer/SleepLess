package mods.thecomputerizer.sleepless.registry.entities.pathfinding;

import mcp.MethodsReturnNonnullByDefault;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class PhantomNodeProcessor extends NodeProcessor {

    protected Block[] unpassableBlocks;

    public void init(IBlockAccess blockCache, EntityLiving entity) {
        super.init(blockCache,entity);
        this.unpassableBlocks = SleepLessConfigHelper.getPhantomPathfindBlacklist();
    }

    protected BlockPos.MutableBlockPos getFlooredEntityPos(int y) {
        return new BlockPos.MutableBlockPos(MathHelper.floor(this.entity.posX),y,MathHelper.floor(this.entity.posZ));
    }

    protected void setFlooredEntityPos(BlockPos.MutableBlockPos pos, int y) {
        pos.setPos(MathHelper.floor(this.entity.posX),y,MathHelper.floor(this.entity.posZ));
    }

    protected BlockPos getEntityBoundedPos(boolean minX, double y, boolean minZ) {
        AxisAlignedBB aabb = entity.getEntityBoundingBox();
        return new BlockPos(minX ? aabb.minX : aabb.maxX,y,minZ ? aabb.minZ : aabb.maxZ);
    }

    protected BlockPos posFromPoint(PathPoint point) {
        return new BlockPos(point.x,point.y,point.z);
    }

    protected EnumFacing getFacing(int offsetX, int offsetZ) {
        boolean isfacingX = Math.abs(offsetX)>=Math.abs(offsetZ);
        return isfacingX ? (offsetX>=0 ? EnumFacing.EAST : EnumFacing.WEST) : (offsetZ>=0 ? EnumFacing.SOUTH : EnumFacing.NORTH);
    }

    protected double blockHeight(BlockPos pos) {
        return this.blockaccess.getBlockState(pos).getBoundingBox(this.blockaccess,pos).maxY;
    }

    protected boolean isAir(BlockPos pos) {
        if(Objects.isNull(this.blockaccess)) return true;
        return this.blockaccess.getBlockState(pos).getMaterial()==Material.AIR;
    }

    protected boolean isAirOrPassable(BlockPos pos) {
        if(Objects.isNull(this.blockaccess)) return true;
        IBlockState state = this.blockaccess.getBlockState(pos);
        return state.getMaterial()==Material.AIR || state.getBlock().isPassable(this.blockaccess,pos);
    }

    protected boolean isWater(BlockPos pos) {
        if(Objects.isNull(this.blockaccess)) return false;
        Block block = this.blockaccess.getBlockState(pos).getBlock();
        return block==Blocks.WATER || block==Blocks.FLOWING_WATER;
    }

    protected boolean isLava(BlockPos pos) {
        if(Objects.isNull(this.blockaccess)) return false;
        Block block = this.blockaccess.getBlockState(pos).getBlock();
        return block==Blocks.LAVA || block==Blocks.FLOWING_LAVA;
    }

    protected boolean intersectsUnpassable(AxisAlignedBB aabb) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for(int x=MathHelper.floor(aabb.minX)-1; x<MathHelper.ceil(aabb.maxX)+1; x++) {
            for(int y=MathHelper.floor(aabb.minY)-1; y<MathHelper.ceil(aabb.maxY)+1; y++) {
                for(int z=MathHelper.floor(aabb.minZ)-1; z<MathHelper.ceil(aabb.maxZ)+1; z++) {
                    pos.setPos(x,y,z);
                    if(!isPassable(pos)) return true;
                }
            }
        }
        return false;
    }

    protected boolean isPassable(BlockPos pos) {
        if(Objects.isNull(this.blockaccess)) return false;
        if(Objects.isNull(this.unpassableBlocks) || this.unpassableBlocks.length==0) return true;
        Block block = this.blockaccess.getBlockState(pos).getBlock();
        for(Block unpassable : this.unpassableBlocks)
            if(block==unpassable) return false;
        return true;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess cache, int x, int y, int z) {
        BlockPos pos = new BlockPos(x,y,z);
        if(isAir(pos)) return PathNodeType.OPEN;
        if(isWater(pos)) return PathNodeType.WATER;
        if(isLava(pos)) return PathNodeType.LAVA;
        if(cache.getBlockState(pos).getBlock().isPassable(cache,pos)) return PathNodeType.OPEN;
        return !isPassable(pos) || y<this.entity.getPosition().getY() ? PathNodeType.BLOCKED : PathNodeType.OPEN;
    }

    protected int tryAddingPoint(PathPoint[] points, @Nullable PathPoint point, int index, PathPoint target, float dist) {
        if(Objects.nonNull(point) && !point.visited && point.distanceTo(target)<dist)
            points[index++]= point;
        return index;
    }

    protected PathNodeType getPathNodeType(EntityLiving entity, BlockPos pos) {
        return this.getPathNodeType(entity,pos.getX(),pos.getY(),pos.getZ());
    }

    protected PathNodeType getPathNodeType(EntityLiving entity, int x, int y, int z) {
        return this.getPathNodeType(this.blockaccess,x,y,z,entity,this.entitySizeX,this.entitySizeY,this.entitySizeZ,
                this.getCanOpenDoors(),this.getCanEnterDoors());
    }

    protected Vec3d[] getSortedPointsAround(int x, int y, int z, Vec3d targetPoint) {
        List<Vec3d> foundPoints = new ArrayList<>();
        for(int x1=x-1; x1<x+2; x1++)
            for(int z1=z-1; z1<z+2; z1++)
                if(x1!=x && z1!=z)
                    foundPoints.add(new Vec3d(x,y,z));
        foundPoints.sort(Comparator.comparingDouble(targetPoint::distanceTo));
        Collections.reverse(foundPoints);
        return foundPoints.toArray(new Vec3d[0]);
    }
}
