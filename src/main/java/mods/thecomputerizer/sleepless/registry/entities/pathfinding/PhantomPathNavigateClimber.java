package mods.thecomputerizer.sleepless.registry.entities.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.world.World;

public class PhantomPathNavigateClimber extends PathNavigateClimber {
    public PhantomPathNavigateClimber(EntityLiving entity, World world) {
        super(entity,world);
    }
}
