package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientEffects {
    public static final ResourceLocation GRAYSCALE_SHADER = Constants.res("shaders/post/dynamic_color_overlay.json");
    private static final Random RANDOM = new Random();
    public static float FOV_ADJUST = 0f;
    public static float COLOR_CORRECTION = 0f;
    public static float AMBIENT_SOUND_CHANCE = 0f;
    public static float SCREEN_SHAKE = 0f;
    public static float QUIET_SOUNDS = 1f;
    public static float LIGHT_DIMMING = 0f;

    public static float getFOVAdjustment(float fov) {
        return fov*((FOV_ADJUST/2f)+0.5f);
    }

    public static boolean isScreenShaking() {
        return SCREEN_SHAKE>0;
    }

    public static float getScreenShake(boolean positive) {
        float factor = (1f-SCREEN_SHAKE)/2f;
        return positive ? factor : factor*-1;
    }

    public static void tryAmbientSound() {
        WorldClient world = Minecraft.getMinecraft().world;
        if(Objects.nonNull(world) && RANDOM.nextFloat()<=AMBIENT_SOUND_CHANCE/100f) {
            Constants.testLog("YOOO CUSTOM SOUND");
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            Vec3d playAt = getRandomAround(player.posX,player.posY,player.posZ);
            world.playSound(null,playAt.x,playAt.y,playAt.z,SoundEvents.AMBIENT_CAVE,
                    SoundCategory.AMBIENT, 1f,(float)(1d+doubleRand(0d)/50d));
        }
    }

    private static Vec3d getRandomAround(double x, double y, double z) {
        return new Vec3d(doubleRand(x),doubleRand(y),doubleRand(z));
    }

    private static double doubleRand(double d) {
        double rand = RANDOM.nextDouble()*5d*2d;
        if(rand>5d) rand = -1d*(rand-5d);
        return d+rand;
    }
}