package mods.thecomputerizer.sleepless.registry.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import mods.thecomputerizer.sleepless.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

import java.util.Objects;

@SuppressWarnings("Guava")
public class EntityWatchClosestWithSleepDebt2 extends EntityAIWatchClosest2 {

    private final float minSleepDebt;
    private final Predicate<Entity> defaultPredicate;

    public EntityWatchClosestWithSleepDebt2(EntityLiving entity, float distance, float sleepDebt, float chance) {
        super(entity,EntityPlayer.class,distance,chance);
        this.minSleepDebt = sleepDebt;
        this.defaultPredicate = setDefaultPredicate();
    }

    public EntityWatchClosestWithSleepDebt2(EntityAIWatchClosest instance, float minSleepDebt) {
        super(instance.entity,instance.watchedClass,instance.maxDistance,instance.chance);
        this.minSleepDebt = minSleepDebt;
        this.defaultPredicate = setDefaultPredicate();
    }

    private Predicate<Entity> setDefaultPredicate() {
        return Predicates.and(EntitySelectors.NOT_SPECTATING,EntitySelectors.notRiding(this.entity));
    }

    @Override
    public boolean shouldExecute() {
        if(this.entity instanceof NightTerrorEntity && ((NightTerrorEntity)this.entity).getAnimationData()
                .currentAnimation!=NightTerrorEntity.AnimationType.IDLE) return false;
        if(this.entity.getRNG().nextFloat() >= this.chance) return false;
        else {
            if(Objects.nonNull(this.entity.getAttackTarget())) this.closestEntity = this.entity.getAttackTarget();
            if(this.watchedClass==EntityPlayer.class)
                this.closestEntity = EntityUtil.getClosestPlayerWithSleepDebt(this.minSleepDebt,this.entity.world,
                        this.entity.getPositionVector(),this.maxDistance,this.defaultPredicate);
            return Objects.nonNull(this.closestEntity);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        if(this.entity instanceof NightTerrorEntity && ((NightTerrorEntity)this.entity).getAnimationData()
                .currentAnimation!=NightTerrorEntity.AnimationType.IDLE) return false;
        return super.shouldContinueExecuting();
    }
}
