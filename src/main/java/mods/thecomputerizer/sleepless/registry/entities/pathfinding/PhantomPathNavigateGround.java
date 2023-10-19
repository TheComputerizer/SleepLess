package mods.thecomputerizer.sleepless.registry.entities.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class PhantomPathNavigateGround extends PathNavigateGround {

    public PhantomPathNavigateGround(EntityLiving entity, World world) {
        super(entity,world);
    }
}
