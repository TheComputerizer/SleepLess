package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.client.render.geometry.Column;
import mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

import java.util.Objects;

import static mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender.STATIC_RENDERS;

/**
 * The class itself shouldn't be annotated with SideOnly but all methods and parameters should be
 */
public class ClientPacketHandlers {

    @SideOnly(Side.CLIENT)
    public static void updateClientEffects(float grayscale, float ambientChance, float quietSounds, float lightDim,
                                           float fogDensity, float walkSpeed, float breathingFactor, float miningSpeed,
                                           float phantomVisibility) {
        ClientEffects.COLOR_CORRECTION = grayscale;
        ClientEffects.AMBIENT_SOUND_CHANCE = ambientChance;
        ClientEffects.QUIET_SOUNDS = quietSounds;
        ClientEffects.LIGHT_DIMMING = lightDim;
        ClientEffects.FOG_DENSITY = fogDensity;
        ClientEffects.WALK_SPEED = 0.1f;
        if(SleepLessConfigHelper.shouldWalkSlower()) ClientEffects.WALK_SPEED *= walkSpeed;
        ClientEffects.BREATHING_FACTOR = breathingFactor;
        ClientEffects.MINING_SPEED = miningSpeed;
        ClientEffects.PHANTOM_VISIBILITY = phantomVisibility;
        Minecraft mc = Minecraft.getMinecraft();
        ((SoundSystem)mc.getSoundHandler().sndManager.sndSystem).setMasterVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
        if(Objects.nonNull(mc.player)) mc.player.capabilities.setPlayerWalkSpeed(ClientEffects.WALK_SPEED);
        if(ClientEffects.BREATHING_FACTOR==0) ClientEffects.FOV_ADJUST = 0f;
    }

    @SideOnly(Side.CLIENT)
    public static void testColumnRender(Vec3d pos) {
        StaticGeometryRender render = new StaticGeometryRender(Minecraft.getMinecraft().getRenderManager(),pos);
        render.addColumn(new Column(Minecraft.getMinecraft().world.rand,new Vec3d(0d,-1d,0d),500d,10d,7.5d));
        synchronized (STATIC_RENDERS) {
            STATIC_RENDERS.add(render);
        }
    }
}
