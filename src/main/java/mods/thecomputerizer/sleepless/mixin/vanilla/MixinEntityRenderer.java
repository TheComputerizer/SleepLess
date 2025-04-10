package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static mods.thecomputerizer.sleepless.client.render.ClientEffects.FOG_DENSITY;
import static mods.thecomputerizer.sleepless.client.render.ClientEffects.LIGHT_DIMMING;
import static mods.thecomputerizer.sleepless.registry.PotionRegistry.PHASED;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.PUTFIELD;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow private float farPlaneDistance;

    @Redirect(at=@At(value="FIELD",target="Lnet/minecraft/client/settings/GameSettings;gammaSetting:F",
            opcode=GETFIELD),method="updateLightmap")
    private float sleepless$redirectUpdateLightmap(GameSettings settings) {
        return SleepLessConfigHelper.shouldDimLight() && LIGHT_DIMMING>0 ? 0f : settings.gammaSetting;
    }

    @Redirect(at=@At(value="FIELD",target="Lnet/minecraft/client/renderer/EntityRenderer;farPlaneDistance:F",
            opcode=GETFIELD,ordinal=0),method="setupFog")
    private float sleepless$redirectFarplane1(EntityRenderer renderer) {
        return SleepLessConfigHelper.shouldIncreaseFog() ?
                NightTerrorClient.overrideFarplane(this.farPlaneDistance/(1f+FOG_DENSITY)) :
                this.farPlaneDistance;
    }

    @Redirect(at=@At(value="FIELD",target="Lnet/minecraft/client/renderer/EntityRenderer;farPlaneDistance:F",
            opcode=GETFIELD,ordinal=1),method="setupFog")
    private float sleepless$redirectFarplane2(EntityRenderer renderer) {
        return SleepLessConfigHelper.shouldIncreaseFog() ?
                NightTerrorClient.overrideFarplane(this.farPlaneDistance/(1f+FOG_DENSITY)) :
                this.farPlaneDistance;
    }

    @Redirect(at = @At(value="FIELD",target="Lnet/minecraft/client/renderer/EntityRenderer;pointedEntity:"+
            "Lnet/minecraft/entity/Entity;",ordinal=1,opcode=PUTFIELD),method="getMouseOver")
    private void sleepless$phantomHack1(EntityRenderer renderer, Entity entity) {
        if(!(entity instanceof EntityLivingBase) ||
                !((EntityLivingBase)entity).isPotionActive(PHASED))
            renderer.pointedEntity = entity;
    }

    @Redirect(at=@At(value="FIELD",target="Lnet/minecraft/client/renderer/EntityRenderer;pointedEntity:"+
            "Lnet/minecraft/entity/Entity;",ordinal=2,opcode=PUTFIELD),method="getMouseOver")
    private void sleepless$phantomHack2(EntityRenderer renderer, Entity entity) {
        if(!(entity instanceof EntityLivingBase) ||
                !((EntityLivingBase)entity).isPotionActive(PHASED))
            renderer.pointedEntity = entity;
    }

    @Redirect(at=@At(value="FIELD",target="Lnet/minecraft/client/renderer/EntityRenderer;pointedEntity:"+
            "Lnet/minecraft/entity/Entity;",ordinal=3,opcode=PUTFIELD),method="getMouseOver")
    private void sleepless$phantomHack3(EntityRenderer renderer, Entity entity) {
        if(!(entity instanceof EntityLivingBase) ||
                !((EntityLivingBase)entity).isPotionActive(PHASED))
            renderer.pointedEntity = entity;
    }
}