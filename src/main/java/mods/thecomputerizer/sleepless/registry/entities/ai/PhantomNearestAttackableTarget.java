package mods.thecomputerizer.sleepless.registry.entities.ai;

import mods.thecomputerizer.sleepless.util.EntityUtil;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class PhantomNearestAttackableTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

    public PhantomNearestAttackableTarget(EntityCreature creature, Class<T> targetClass, int chance, boolean checkSight,
                                          boolean onlyNearby,
                                          float minSleepDebt) {
        super(creature,targetClass,chance,checkSight,onlyNearby,EntityUtil.getSleepDebtPredicate(minSleepDebt));
    }

    public PhantomNearestAttackableTarget(EntityAINearestAttackableTarget<T> instance, float minSleepDebt) {
        super(instance.taskOwner,instance.targetClass,instance.targetChance,instance.shouldCheckSight,
                instance.nearbyOnly,EntityUtil.getSleepDebtPredicate(minSleepDebt));
    }
}
