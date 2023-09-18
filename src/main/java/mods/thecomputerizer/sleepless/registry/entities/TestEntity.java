package mods.thecomputerizer.sleepless.registry.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class TestEntity extends EntityLiving {

    public TestEntity(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.setSize(1f, 1.875f);
        this.isImmuneToFire = true;
        this.experienceValue = 99;
    }
}
