package mods.thecomputerizer.sleepless.world.ai;

import com.google.common.base.Predicates;
import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntitySelectors;

import java.util.Objects;

public class EntityWatchClosestWithSleepDebt extends EntityAIWatchClosest {


    private final float minSleepDebt;
    public EntityWatchClosestWithSleepDebt(EntityLiving entity, float distance, float sleepDebt) {
        super(entity,EntityPlayer.class,distance,1f);
        this.minSleepDebt = sleepDebt;
    }

    @SuppressWarnings({"unchecked", "Guava"})
    @Override
    public boolean shouldExecute() {
        if(this.entity instanceof NightTerrorEntity && ((NightTerrorEntity)this.entity).getAnimationData()
                .currentAnimation!=NightTerrorEntity.AnimationType.IDLE) return false;
        if(this.entity.getRNG().nextFloat() >= this.chance) return false;
        else {
            if(Objects.nonNull(this.entity.getAttackTarget())) this.closestEntity = this.entity.getAttackTarget();
            if(this.watchedClass==EntityPlayer.class)
                this.closestEntity = this.entity.world.getClosestPlayer(this.entity.posX,this.entity.posY,this.entity.posZ,
                        this.maxDistance,Predicates.and(EntitySelectors.NOT_SPECTATING,EntitySelectors.notRiding(this.entity),
                                input -> input instanceof EntityPlayerMP &&
                                        CapabilityHandler.getSleepDebt((EntityPlayerMP)input)>=minSleepDebt));
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
