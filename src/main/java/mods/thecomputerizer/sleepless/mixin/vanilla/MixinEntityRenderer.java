package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;gammaSetting:F",
            opcode = Opcodes.GETFIELD), method = "updateLightmap")
    private float sleepless$redirectUpdateLightmap(GameSettings settings) {
        return ClientEffects.LIGHT_DIMMING>0 ? 0f : settings.gammaSetting;
    }
}
