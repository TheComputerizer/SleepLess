package mods.thecomputerizer.sleepless.registry.entities.phantom;

import mods.thecomputerizer.sleepless.common.WorldEvents;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public class PhantomSpawnEntry extends Biome.SpawnListEntry {

    private final float despawnDistance;

    public PhantomSpawnEntry(Class<? extends EntityLiving> entityClass, int weight, int minGroup, int maxGroup,
                             float despawnDistance) {
        super(entityClass,weight,minGroup,maxGroup);
        this.despawnDistance = despawnDistance;
    }

    @Override
    public @Nonnull EntityLiving newInstance(@Nonnull World world) throws Exception {
        EntityLiving entity = super.newInstance(world);
        if(entity instanceof PhantomEntity) {
            PhantomEntity phantom = (PhantomEntity)entity;
            phantom.setDespawnDistance(this.despawnDistance);
            float chance = (this.despawnDistance/WorldEvents.MAX_PHANTOM_DESPAWN_RANGE)*2f;
            if(chance<1f && world.rand.nextFloat()<(1f-chance)/2f) phantom.markAggressive();
        }
        return entity;
    }
}
