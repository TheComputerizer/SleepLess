package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.client.render.geometry.Column;
import mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

import java.util.Objects;

import static mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender.STATIC_RENDERS;
import static mods.thecomputerizer.sleepless.client.render.ClientEffects.*;
import static net.minecraft.util.SoundCategory.MASTER;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

/**
 * The class itself shouldn't be annotated with SideOnly but all methods and parameters should be
 */
public class ClientPacketHandlers {

    @SideOnly(CLIENT)
    public static void updateClientEffects(float grayscale, float ambientChance, float quietSounds, float lightDim,
            float fogDensity, float walkSpeed, float breathingFactor, float miningSpeed, float phantomVisibility) {
        COLOR_CORRECTION = grayscale;
        AMBIENT_SOUND_CHANCE = ambientChance;
        QUIET_SOUNDS = quietSounds;
        LIGHT_DIMMING = lightDim;
        FOG_DENSITY = fogDensity;
        WALK_SPEED = 0.1f;
        if(SleepLessConfigHelper.shouldWalkSlower()) WALK_SPEED *= walkSpeed;
        BREATHING_FACTOR = breathingFactor;
        MINING_SPEED = miningSpeed;
        PHANTOM_VISIBILITY = phantomVisibility;
        Minecraft mc = Minecraft.getMinecraft();
        ((SoundSystem)mc.getSoundHandler().sndManager.sndSystem).setMasterVolume(mc.gameSettings.getSoundLevel(MASTER));
        if(Objects.nonNull(mc.player)) mc.player.capabilities.setPlayerWalkSpeed(WALK_SPEED);
        if(BREATHING_FACTOR==0) FOV_ADJUST = 0f;
    }

    @SideOnly(CLIENT)
    public static void testColumnRender(Vec3d pos) {
        StaticGeometryRender render = new StaticGeometryRender(Minecraft.getMinecraft().getRenderManager(),pos);
        World world = Minecraft.getMinecraft().world;
        Vec3d center = new Vec3d(0d,-1d,0d);
        render.addColumn(new Column(world.rand,center,500d,10d,7.5d));
        synchronized(STATIC_RENDERS) {
            STATIC_RENDERS.add(render);
        }
    }
}