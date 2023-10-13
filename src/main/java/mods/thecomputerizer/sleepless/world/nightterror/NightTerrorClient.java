package mods.thecomputerizer.sleepless.world.nightterror;

import mods.thecomputerizer.sleepless.client.render.geometry.Column;
import mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import paulscode.sound.SoundSystem;

import java.util.Objects;

import static mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender.STATIC_RENDERS;

public class NightTerrorClient {

    public static StaticGeometryRender GEOMETRY_RENDER;
    private static final Vec3d[] BELL_COLUMNS = new Vec3d[]{new Vec3d(100d,-30d,-100d),
            new Vec3d(-100d,-30d,-100d),new Vec3d(100d,-30d,100d),
            new Vec3d(-100d,-30d,100d),new Vec3d(33d,-30d,-100d),
            new Vec3d(33d,-30d,100d),new Vec3d(-33d,-30d,-100d),
            new Vec3d(-33d,-30d,100d),new Vec3d(-100d,-30d,33d),
            new Vec3d(100d,-30d,33d), new Vec3d(-100d,-30d,-33d),
            new Vec3d(100d,-30d,-33d)};
    private static boolean silenceMusicTicker = false;
    private static float fogOverride;
    private static float colorOverride;

    public static void setClientEffect(boolean silenceMusic, float fog, float color, int columnRender) {
        silenceMusicTicker = silenceMusic;
        fogOverride = fog;
        colorOverride = color;
        Minecraft mc = Minecraft.getMinecraft();
        ((SoundSystem)mc.getSoundHandler().sndManager.sndSystem).setMasterVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
        if(columnRender>0) addColumn(columnRender);
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
        synchronized (STATIC_RENDERS) {
            if (Objects.isNull(GEOMETRY_RENDER)) {
                GEOMETRY_RENDER = new StaticGeometryRender(Minecraft.getMinecraft().getRenderManager(),
                        Minecraft.getMinecraft().player.getPositionVector());
                STATIC_RENDERS.add(GEOMETRY_RENDER);
            }
            Column column = new Column(Minecraft.getMinecraft().world.rand,BELL_COLUMNS[columnRender].scale(0.3f),500d,10d,7.5d);
            GEOMETRY_RENDER.addColumn(column);
        }
    }

    public static float overrideFog(float original) {
        return colorOverride==0f ? original : fogOverride;
    }

    public static float overrideRed(float original) {
        return colorOverride==0f ? original : colorOverride;
    }

    public static float overrideNotRed(float original) {
        return colorOverride==0f ? original : 1f-colorOverride;
    }
}
