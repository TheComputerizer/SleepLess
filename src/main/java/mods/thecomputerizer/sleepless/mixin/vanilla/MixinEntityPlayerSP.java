package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Unique private boolean sleepless$hasPhasedEffect() {
        return ((EntityPlayerSP)(Object)this).isPotionActive(PotionRegistry.PHASED);
    }

    @Inject(at = @At("HEAD"), method = "pushOutOfBlocks", cancellable = true)
    private void sleepless$pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if(sleepless$hasPhasedEffect() || ((EntityPlayerSP)(Object)this).noClip) cir.setReturnValue(false);
    }
}
