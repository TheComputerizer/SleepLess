package mods.thecomputerizer.sleepless.registry.entities.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.world.World;

public class PhantomPathNavigateFlying extends PathNavigateFlying {


    public PhantomPathNavigateFlying(EntityLiving entity, World world) {
        super(entity,world);
    }
}
