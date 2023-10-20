package mods.thecomputerizer.sleepless.registry.entities.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PhantomPathNavigateGround extends PathNavigateGround {

    public PhantomPathNavigateGround(EntityLiving entity, World world) {
        super(entity,world);
    }

    @Override
    protected @Nonnull PathFinder getPathFinder() {
        this.nodeProcessor = new PhantomWalkNodeProcessor();
        return new PathFinder(this.nodeProcessor);
    }
}
