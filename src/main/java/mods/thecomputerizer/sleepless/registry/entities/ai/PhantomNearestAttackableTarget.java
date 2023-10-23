package mods.thecomputerizer.sleepless.registry.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import mods.thecomputerizer.sleepless.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("Guava")
public class PhantomNearestAttackableTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

    private static Predicate<? super EntityLivingBase> makePredicate(float minSleepDebt, @Nullable Supplier<Boolean> extraPredicate) {
        Predicate<Entity> sleepDebtPredicate = EntityUtil.getSleepDebtPredicate(minSleepDebt);
        if(Objects.isNull(extraPredicate)) return sleepDebtPredicate;
        return Predicates.and(sleepDebtPredicate,entity -> extraPredicate.get());
    }

    public PhantomNearestAttackableTarget(EntityCreature creature, Class<T> targetClass, int chance, boolean checkSight,
                                          boolean onlyNearby, float minSleepDebt, @Nullable Supplier<Boolean> extraPredicate) {
        super(creature,targetClass,chance,checkSight,onlyNearby,makePredicate(minSleepDebt,extraPredicate));
    }

    @SuppressWarnings("unused")
    public PhantomNearestAttackableTarget(EntityAINearestAttackableTarget<T> instance, float minSleepDebt) {
        super(instance.taskOwner,instance.targetClass,instance.targetChance,instance.shouldCheckSight,
                instance.nearbyOnly,EntityUtil.getSleepDebtPredicate(minSleepDebt));
    }
}
