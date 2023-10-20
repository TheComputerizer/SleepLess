package mods.thecomputerizer.sleepless.registry.entities.pathfinding;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class PhantomWalkNodeProcessor extends WalkNodeProcessor {

    private PathNodeType getPathNodeType(EntityLiving entity, int x, int y, int z) {
        return this.getPathNodeType(this.blockaccess,x,y,z,entity,this.entitySizeX,this.entitySizeY,this.entitySizeZ,
                this.getCanOpenDoors(),this.getCanEnterDoors());
    }

    @Override
    protected @Nullable PathPoint getSafePoint(int x, int y, int z, int step, double blockHeight, @Nonnull EnumFacing facing) {
        PathPoint point = null;
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos pos1 = pos.down();
        double blockHeightCopy = (double)y-(1d-this.blockaccess.getBlockState(pos1).getBoundingBox(this.blockaccess,pos1).maxY);
        if(blockHeightCopy-blockHeight>1.125d) return null;
        else {
            PathNodeType type = this.getPathNodeType(this.entity,x,y,z);
            float f = this.entity.getPathPriority(type);
            if(f>=0f) {
                point = this.openPoint(x,y,z);
                point.nodeType = type;
                point.costMalus = Math.max(point.costMalus,f);
            }
            if(type!=PathNodeType.WALKABLE) {
                if(Objects.isNull(point) && step>0 && type != PathNodeType.FENCE && type != PathNodeType.TRAPDOOR)
                    point = this.getSafePoint(x,y+1,z,step-1,blockHeight,facing);
                if(type==PathNodeType.OPEN) {
                    if(this.entity.width>=1f) {
                        PathNodeType type1 = this.getPathNodeType(this.entity,x,y-1,z);
                        if(type1 == PathNodeType.BLOCKED) {
                            point = this.openPoint(x,y,z);
                            point.nodeType = PathNodeType.WALKABLE;
                            point.costMalus = Math.max(point.costMalus,f);
                            return point;
                        }
                    }
                    int i = 0;
                    while(y>0 && type==PathNodeType.OPEN) {
                        --y;
                        if(i++>=this.entity.getMaxFallHeight()) return null;
                        type = this.getPathNodeType(this.entity,x,y,z);
                        f = this.entity.getPathPriority(type);
                        if(type!=PathNodeType.OPEN && f>=0.0F) {
                            point = this.openPoint(x,y,z);
                            point.nodeType = type;
                            point.costMalus = Math.max(point.costMalus,f);
                            break;
                        }
                        if(f<0f) return null;
                    }
                }
            }
            return point;
        }
    }

    @Override
    protected @Nonnull PathNodeType getPathNodeTypeRaw(IBlockAccess world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();
        PathNodeType type = block.getAiPathNodeType(state,world,pos, this.currentEntity);
        if(Objects.nonNull(type)) return type;
        if(material==Material.AIR) return PathNodeType.OPEN;
        else if(block!=Blocks.TRAPDOOR && block!=Blocks.IRON_TRAPDOOR && block!=Blocks.WATERLILY) {
            if(block==Blocks.FIRE) return PathNodeType.DAMAGE_FIRE;
            else if(block==Blocks.CACTUS) return PathNodeType.DAMAGE_CACTUS;
            else if(block instanceof BlockDoor && material == Material.WOOD && !state.getValue(BlockDoor.OPEN))
                return PathNodeType.DOOR_WOOD_CLOSED;
            else if(block instanceof BlockDoor && material == Material.IRON && !state.getValue(BlockDoor.OPEN))
                return PathNodeType.DOOR_IRON_CLOSED;
            else if(block instanceof BlockDoor && state.getValue(BlockDoor.OPEN)) return PathNodeType.DOOR_OPEN;
            else if(block instanceof BlockRailBase) return PathNodeType.RAIL;
            else if(!(block instanceof BlockFence) && !(block instanceof BlockWall) &&
                    (!(block instanceof BlockFenceGate) || state.getValue(BlockFenceGate.OPEN))) {
                if(material == Material.WATER) return PathNodeType.WATER;
                else if (material == Material.LAVA) return PathNodeType.LAVA;
                else return PathNodeType.WALKABLE;
            }
            else return PathNodeType.FENCE;
        }
        return PathNodeType.TRAPDOOR;
    }
}
