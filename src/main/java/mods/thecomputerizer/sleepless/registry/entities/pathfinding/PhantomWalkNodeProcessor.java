package mods.thecomputerizer.sleepless.registry.entities.pathfinding;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PhantomWalkNodeProcessor extends PhantomNodeProcessor {

    @Override
    public PathPoint getStart() {
        int ground;
        if(this.getCanSwim() && entity.isInWater()) {
            ground = (int)this.entity.getEntityBoundingBox().minY;
            BlockPos.MutableBlockPos mutablePos = getFlooredEntityPos(ground);
            while(isWater(mutablePos)) {
                ground++;
                setFlooredEntityPos(mutablePos,ground);
            }
        }
        else if(this.entity.onGround)
            ground = MathHelper.floor(this.entity.getEntityBoundingBox().minY+0.5d);
        else {
            BlockPos pos = new BlockPos(this.entity);
            while(isAir(pos)) pos = pos.down();
            ground = pos.up().getY();
        }
        BlockPos entityPos = new BlockPos(this.entity);
        PathNodeType type = this.getPathNodeType(this.entity,entityPos.getX(),ground,entityPos.getZ());
        if(this.entity.getPathPriority(type)<0f) {
            Set<BlockPos> set = new HashSet<>();
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX,ground,this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX,ground,this.entity.getEntityBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX,ground,this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX,ground,this.entity.getEntityBoundingBox().maxZ));
            for(BlockPos pos : set) {
                type = this.getPathNodeType(this.entity,pos);
                if(this.entity.getPathPriority(type)>=0f) return this.openPoint(pos.getX(),pos.getY(),pos.getZ());
            }
        }
        return this.openPoint(entityPos.getX(),ground,entityPos.getZ());
    }

    @Override
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        return this.openPoint(MathHelper.floor(x),MathHelper.floor(y),MathHelper.floor(z));
    }

    @Override
    public int findPathOptions(PathPoint[] options, PathPoint curPoint, PathPoint targetPoint, float maxDist) {
        int index = 0;
        int stepHeight = 0;
        PathNodeType type = getPathNodeType(this.entity,curPoint.x,curPoint.y+1,curPoint.z);
        if(this.entity.getPathPriority(type)>=0f) stepHeight = MathHelper.floor(Math.max(1f,this.entity.stepHeight));
        BlockPos pos = posFromPoint(curPoint).down();
        double distToBlockTop = (double)curPoint.y-(1d-blockHeight(pos));
        PathPoint southPoint = this.getSafePoint(curPoint.x,curPoint.y,curPoint.z+1,stepHeight,distToBlockTop,EnumFacing.SOUTH);
        PathPoint westPoint = this.getSafePoint(curPoint.x-1,curPoint.y,curPoint.z,stepHeight,distToBlockTop,EnumFacing.WEST);
        PathPoint eastPoint = this.getSafePoint(curPoint.x+1,curPoint.y,curPoint.z,stepHeight,distToBlockTop,EnumFacing.EAST);
        PathPoint northPoint = this.getSafePoint(curPoint.x,curPoint.y,curPoint.z-1,stepHeight,distToBlockTop,EnumFacing.NORTH);
        index = tryAddingPoint(options,southPoint,index,targetPoint,maxDist);
        index = tryAddingPoint(options,westPoint,index,targetPoint,maxDist);
        index = tryAddingPoint(options,eastPoint,index,targetPoint,maxDist);
        index = tryAddingPoint(options,northPoint,index,targetPoint,maxDist);
        boolean openNorth = Objects.isNull(northPoint) || northPoint.nodeType==PathNodeType.OPEN || northPoint.costMalus!=0f;
        boolean openSouth = Objects.isNull(southPoint) || southPoint.nodeType==PathNodeType.OPEN || southPoint.costMalus!=0f;
        boolean openEast = Objects.isNull(eastPoint) || eastPoint.nodeType==PathNodeType.OPEN || eastPoint.costMalus!=0f;
        boolean openWest = Objects.isNull(westPoint) || westPoint.nodeType==PathNodeType.OPEN || westPoint.costMalus!=0f;
        if(openNorth && openWest) {
            PathPoint northWestPoint = this.getSafePoint(curPoint.x-1,curPoint.y,curPoint.z-1,stepHeight,distToBlockTop,EnumFacing.NORTH);
            index = tryAddingPoint(options,northWestPoint,index,targetPoint,maxDist);
        }
        if(openNorth && openEast) {
            PathPoint northEastPoint = this.getSafePoint(curPoint.x+1,curPoint.y,curPoint.z-1,stepHeight,distToBlockTop,EnumFacing.NORTH);
            index = tryAddingPoint(options,northEastPoint,index,targetPoint,maxDist);
        }
        if(openSouth && openWest) {
            PathPoint southWestPoint = this.getSafePoint(curPoint.x-1,curPoint.y,curPoint.z+1,stepHeight,distToBlockTop,EnumFacing.SOUTH);
            index = tryAddingPoint(options,southWestPoint,index,targetPoint,maxDist);
        }
        if(openSouth && openEast) {
            PathPoint southWestPoint = this.getSafePoint(curPoint.x+1,curPoint.y,curPoint.z+1,stepHeight,distToBlockTop,EnumFacing.SOUTH);
            index = tryAddingPoint(options,southWestPoint,index,targetPoint,maxDist);
        }
        return index;
    }

    private @Nullable PathPoint getSafePoint(int x, int y, int z, int stepHeight, double distToBlockTop, EnumFacing facing) {
        PathPoint point = null;
        BlockPos pos = new BlockPos(x,y,z);
        double distToBlockTopBelow = (double)y-(1d-blockHeight(pos.down()));
        if(distToBlockTopBelow-distToBlockTop>9d/8d) return null;
        PathNodeType type = getPathNodeType(this.entity,x,y,z);
        float priority = this.entity.getPathPriority(type);
        double width = (double)this.entity.width/2d;
        if(priority>0f) {
            point = openPoint(x,y,z);
            point.nodeType = type;
            point.costMalus = Math.max(point.costMalus,priority);
        }
        if(type==PathNodeType.WALKABLE) return point;
        if(Objects.isNull(point) && stepHeight> 0 && type!=PathNodeType.FENCE && type!=PathNodeType.TRAPDOOR) {
            point = this.getSafePoint(x,y+1,z,stepHeight-1,distToBlockTop,facing);
            if(Objects.nonNull(point) && (point.nodeType==PathNodeType.OPEN || point.nodeType==PathNodeType.WALKABLE) && this.entity.width<1f) {
                double offsetX = (double)(x-facing.getXOffset())+0.5d;
                double offsetZ = (double)(z-facing.getZOffset())+0.5d;
                AxisAlignedBB aabb = new AxisAlignedBB(offsetX-width,(double)y+0.001d,offsetZ-width,
                        offsetX+width,(float)y+this.entity.height,offsetZ+width);
                AxisAlignedBB blockAABB = this.blockaccess.getBlockState(pos).getBoundingBox(this.blockaccess,pos);
                AxisAlignedBB totalAABB = aabb.expand(0d,blockAABB.maxY-0.002d,0d);
                if(intersectsUnpassable(totalAABB)) point = null;
            }
        }
        if(type==PathNodeType.OPEN) {
            AxisAlignedBB aabb = new AxisAlignedBB((double)x-width+0.5d,(double)y+0.001d,
                    (double)z-width+0.5d,(double)x+width+0.5d,
                    (float)y+this.entity.height,(double)z+width+0.5d);
            if(intersectsUnpassable(aabb)) return null;
            if(width>1f) {
                PathNodeType type1 = this.getPathNodeType(this.entity,x,y-1,z);
                if(type1==PathNodeType.BLOCKED) {
                    point = this.openPoint(x,y,z);
                    point.nodeType = PathNodeType.WALKABLE;
                    point.costMalus = Math.max(point.costMalus,priority);
                    return point;
                }
            }
            int fallHeight = 0;
            while(y>0 && type==PathNodeType.OPEN) {
                y--;
                if(fallHeight++>=this.entity.getMaxFallHeight()) return null;
                type = this.getPathNodeType(this.entity,x,y,z);
                priority = this.entity.getPathPriority(type);
                if(type!=PathNodeType.OPEN && priority>=0f) {
                    point = this.openPoint(pos.getX(),y,pos.getZ());
                    point.nodeType = type;
                    point.costMalus = Math.max(point.costMalus,priority);
                    break;
                }
                if(priority<0f) return null;
            }
        }
        return point;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess cache, int x, int y, int z, EntityLiving entity, int xSize,
                                        int ySize, int zSize, boolean canBreakDoors, boolean canEnterDoors) {
        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType type = PathNodeType.BLOCKED;
        BlockPos pos = new BlockPos(entity);
        type = this.getPathNodeType(cache,x,y,z,xSize,ySize,zSize,canBreakDoors,canEnterDoors,enumset,type,pos);
        if(enumset.contains(PathNodeType.FENCE)) return PathNodeType.FENCE;
        else {
            PathNodeType type1 = PathNodeType.BLOCKED;
            for(PathNodeType type2 : enumset) {
                if(entity.getPathPriority(type2)<0f) return type2;
                if(entity.getPathPriority(type2)>=entity.getPathPriority(type1)) type1 = type2;
            }
            if(type==PathNodeType.OPEN && entity.getPathPriority(type1)==0f) return PathNodeType.OPEN;
            else return type1;
        }
    }

    public PathNodeType getPathNodeType(IBlockAccess cache, int x, int y, int z, int xSize, int ySize, int zSize,
                                        boolean canOpenDoorsIn, boolean canEnterDoorsIn, EnumSet<PathNodeType> set,
                                        PathNodeType type, BlockPos pos) {
        for(int i=0; i<xSize; i++) {
            for(int j=0; j<ySize; j++) {
                for(int k=0; k<zSize; k++) {
                    int l = i+x;
                    int i1 = j+y;
                    int j1 = k+z;
                    PathNodeType type1 = this.getPathNodeType(cache,l,i1,j1);
                    if(type1==PathNodeType.DOOR_WOOD_CLOSED && canOpenDoorsIn && canEnterDoorsIn)
                        type1 = PathNodeType.WALKABLE;
                    if (type1==PathNodeType.RAIL && !(cache.getBlockState(pos).getBlock() instanceof BlockRailBase) &&
                            !(cache.getBlockState(pos.down()).getBlock() instanceof BlockRailBase))
                        type1 = PathNodeType.FENCE;
                    if(i==0 && j==0 && k==0)
                        type = type1;
                    set.add(type1);
                }
            }
        }
        return type;
    }
}
