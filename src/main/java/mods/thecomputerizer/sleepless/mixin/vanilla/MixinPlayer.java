package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public class MixinPlayer {

    @Unique
    private EntityPlayer sleepless$cast() {
        return (EntityPlayer)(Object)this;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addExhaustion(F)V"), method = "addExhaustion")
    private void sleepless$redirectAddExhaustion(FoodStats food, float exhaustion) {
        if(SleepLessConfigHelper.shouldBeHungry()) {
            EntityPlayer player = sleepless$cast();
            if(player instanceof EntityPlayerMP) {
                float adjusted = CapabilityHandler.getHungerAmplifier((EntityPlayerMP)player, exhaustion);
                food.addExhaustion(adjusted);
            }
        } else food.addExhaustion(exhaustion);
    }
}
