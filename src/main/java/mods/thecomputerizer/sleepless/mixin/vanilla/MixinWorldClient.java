package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z", ordinal = 0), method = "tick")
    private boolean sleepless$redirectDoDaylightCycle(GameRules instance, String name) {
        return instance.getBoolean(name) && !NightTerrorClient.shouldDaylightCycle();
    }
}
