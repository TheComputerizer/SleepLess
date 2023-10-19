package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(at = @At("RETURN"), method = "getAmbientMusicType", cancellable = true)
    private void sleepless$getAmbientMusicType(CallbackInfoReturnable<MusicTicker.MusicType> cir) {
        cir.setReturnValue(NightTerrorClient.getMusicOverride(cir.getReturnValue()));
    }
}
