package mods.thecomputerizer.sleepless.world.nightterror;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.client.render.geometry.Column;
import mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender;
import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import paulscode.sound.SoundSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender.STATIC_RENDERS;

public class NightTerrorClient {
    private static final Vec3d[] BELL_COLUMNS = new Vec3d[]{new Vec3d(100d,-150d,-100d).scale(2.2d/3d),
            new Vec3d(-100d,-150d,-100d).scale(2.2d/3d),new Vec3d(100d,-150d,100d).scale(2.2d/3d),
            new Vec3d(-100d,-150d,100d).scale(2.2d/3d),new Vec3d(33d,-100d,-100d),
            new Vec3d(33d,-100d,100d),new Vec3d(-33d,-100d,-100d),
            new Vec3d(-33d,-100d,100d),new Vec3d(-100d,-100d,33d),
            new Vec3d(100d,-100d,33d), new Vec3d(-100d,-100d,-33d),
            new Vec3d(100d,-100d,-33d)};
    private static final List<Integer> CACHED_COLUMN_RENDERS = new ArrayList<>();
    public static StaticGeometryRender GEOMETRY_RENDER;
    private static boolean hasCachedRenders = false;
    private static boolean silenceMusicTicker = false;
    private static float fogOverride;
    private static float colorOverride;

    public static void onClientDisconnected() {
        hasCachedRenders = false;
        silenceMusicTicker = false;
        fogOverride = 0f;
        colorOverride = 0f;
        if(Objects.nonNull(GEOMETRY_RENDER)) {
            synchronized(STATIC_RENDERS) {
                STATIC_RENDERS.remove(GEOMETRY_RENDER);
                GEOMETRY_RENDER = null;
            }
        }
        Minecraft mc = Minecraft.getMinecraft();
        ((SoundSystem)mc.getSoundHandler().sndManager.sndSystem).setMasterVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
    }

    public static void setClientEffect(boolean silenceMusic, float fog, float color, int columnRender, boolean isCatchUp) {
        silenceMusicTicker = silenceMusic;
        fogOverride = fog;
        colorOverride = color;
        Minecraft mc = Minecraft.getMinecraft();
        ((SoundSystem)mc.getSoundHandler().sndManager.sndSystem).setMasterVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
        if(columnRender>=0) {
            if(isCatchUp) {
                for(int i=0; i<=columnRender; i++) addColumn(i);
            } else addColumn(columnRender);
        }
        else if(!silenceMusic && Objects.nonNull(GEOMETRY_RENDER)) {
            synchronized(STATIC_RENDERS) {
                STATIC_RENDERS.remove(GEOMETRY_RENDER);
                GEOMETRY_RENDER = null;
            }
        }
    }

    public static boolean isSilencingMusic() {
        return silenceMusicTicker;
    }

    private static void addColumn(int columnRender) {
        Minecraft mc = Minecraft.getMinecraft();
        if(Objects.isNull(mc.world)) {
            CACHED_COLUMN_RENDERS.add(columnRender);
            hasCachedRenders = true;
            return;
        }
        synchronized (STATIC_RENDERS) {
            if (Objects.isNull(GEOMETRY_RENDER)) {
                GEOMETRY_RENDER = new StaticGeometryRender(Minecraft.getMinecraft().getRenderManager(),
                        mc.player.getPositionVector());
                STATIC_RENDERS.add(GEOMETRY_RENDER);
            }
            Column column = new Column(mc.world.rand, BELL_COLUMNS[columnRender]
                    .scale(0.3f),1000d,10d,7.5d);
            column.setSpeed(1.25d);
            GEOMETRY_RENDER.addColumn(column);
        }
    }

    public static void checkRenderCache() {
        if(!hasCachedRenders) return;
        for(Integer index : CACHED_COLUMN_RENDERS) addColumn(index);
        hasCachedRenders = false;
    }

    public static boolean shouldDaylightCycle() {
        return silenceMusicTicker && colorOverride>0f;
    }

    public static float overrideFog(float original) {
        return fogOverride==0f ? original : fogOverride;
    }

    public static float overrideFarplane(float original) {
        return fogOverride==0f ? original : 72f;
    }

    public static float overrideRed(float original) {
        return colorOverride==0f ? original : colorOverride;
    }

    public static float overrideNotRed(float original) {
        return colorOverride==0f ? original : 1f-colorOverride;
    }

    public static float overrideProminence(float original) {
        return withScreenShake(colorOverride>0f,original,original/2f);
    }

    public static float overrideGrayscale(float original) {
        return withScreenShake(colorOverride>0f,original,Math.max(1f-(ClientEffects.COLOR_CORRECTION/4f),0.75f));
    }

    public static float overrideBrightness(float original) {
        return withScreenShake(colorOverride>0f,original,
                MathHelper.clamp(1f-(0.5f-(ClientEffects.LIGHT_DIMMING/2f)),0.5f,1f));
    }

    private static float withScreenShake(boolean overrideAnyways, float orginal, float adjusted) {
        return overrideAnyways ? adjusted : orginal+((adjusted-orginal)*MathHelper.clamp(ClientEffects.SCREEN_SHAKE,0f,1f));
    }
}
