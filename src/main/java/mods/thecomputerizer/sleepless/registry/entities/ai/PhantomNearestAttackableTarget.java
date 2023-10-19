package mods.thecomputerizer.sleepless.registry.entities.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("Guava")
public class PhantomNearestAttackableTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

    private final float minSleepDebt;

    public PhantomNearestAttackableTarget(EntityCreature creature, Class<T> targetClass, int chance, boolean checkSight,
                                          boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector,
                                          float minSleepDebt) {
        super(creature,targetClass,chance,checkSight,onlyNearby,targetSelector);
        this.minSleepDebt = minSleepDebt;
    }

    public PhantomNearestAttackableTarget(EntityAINearestAttackableTarget<T> instance, float minSleepDebt) {
        super(instance.taskOwner,instance.targetClass,instance.targetChance,instance.shouldCheckSight,
                instance.nearbyOnly,instance.targetEntitySelector);
        this.minSleepDebt = minSleepDebt;
    }

    @Override
    protected boolean canEasilyReach(@Nonnull EntityLivingBase target)
    {
        this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
        Path path = this.taskOwner.getNavigator().getPathToEntityLiving(target);

        if (path == null)
        {
            return false;
        }
        else
        {
            PathPoint pathpoint = path.getFinalPathPoint();

            if (pathpoint == null)
            {
                return false;
            }
            else
            {
                int i = pathpoint.x - MathHelper.floor(target.posX);
                int j = pathpoint.z - MathHelper.floor(target.posZ);
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}
