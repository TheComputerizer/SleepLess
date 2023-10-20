package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(" +
            "Lnet/minecraft/util/DamageSource;F)Z", ordinal = 0), method = "onEntityUpdate")
    private boolean sleepless$redirectSuffocation(EntityLivingBase entity, DamageSource source, float amount) {
        return !entity.isPotionActive(PotionRegistry.PHASED) && entity.attackEntityFrom(source,amount);
    }
}
