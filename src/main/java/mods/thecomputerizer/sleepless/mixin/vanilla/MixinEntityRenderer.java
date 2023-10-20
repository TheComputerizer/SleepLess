package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow private float farPlaneDistance;

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;gammaSetting:F",
            opcode = Opcodes.GETFIELD), method = "updateLightmap")
    private float sleepless$redirectUpdateLightmap(GameSettings settings) {
        return SleepLessConfigHelper.shouldDimLight() && ClientEffects.LIGHT_DIMMING>0 ? 0f : settings.gammaSetting;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;farPlaneDistance:F",
            opcode = Opcodes.GETFIELD, ordinal = 0), method = "setupFog")
    private float sleepless$redirectFarplane1(EntityRenderer renderer) {
        return SleepLessConfigHelper.shouldIncreaseFog() ?
                NightTerrorClient.overrideFarplane(this.farPlaneDistance/(1f+ClientEffects.FOG_DENSITY)) :
                this.farPlaneDistance;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;farPlaneDistance:F",
            opcode = Opcodes.GETFIELD, ordinal = 1), method = "setupFog")
    private float sleepless$redirectFarplane2(EntityRenderer renderer) {
        return SleepLessConfigHelper.shouldIncreaseFog() ?
                NightTerrorClient.overrideFarplane(this.farPlaneDistance/(1f+ClientEffects.FOG_DENSITY)) :
                this.farPlaneDistance;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;pointedEntity:"+
            "Lnet/minecraft/entity/Entity;", ordinal = 1, opcode = Opcodes.PUTFIELD), method = "getMouseOver")
    private void sleepless$phantomHack1(EntityRenderer renderer, Entity entity) {
        if(!(entity instanceof EntityLivingBase) ||
                !((EntityLivingBase)entity).isPotionActive(PotionRegistry.PHASED))
            renderer.pointedEntity = entity;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;pointedEntity:"+
            "Lnet/minecraft/entity/Entity;", ordinal = 2, opcode = Opcodes.PUTFIELD), method = "getMouseOver")
    private void sleepless$phantomHack2(EntityRenderer renderer, Entity entity) {
        if(!(entity instanceof EntityLivingBase) ||
                !((EntityLivingBase)entity).isPotionActive(PotionRegistry.PHASED))
            renderer.pointedEntity = entity;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;pointedEntity:"+
            "Lnet/minecraft/entity/Entity;", ordinal = 3, opcode = Opcodes.PUTFIELD), method = "getMouseOver")
    private void sleepless$phantomHack3(EntityRenderer renderer, Entity entity) {
        if(!(entity instanceof EntityLivingBase) ||
                !((EntityLivingBase)entity).isPotionActive(PotionRegistry.PHASED))
            renderer.pointedEntity = entity;
    }
}
