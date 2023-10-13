package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientEffects {
    public static final ResourceLocation GRAYSCALE_SHADER = Constants.res("shaders/post/dynamic_color_overlay.json");
    private static final Random RANDOM = new Random();
    private static final List<SoundEvent> AMBIENT_SOUNDS = Arrays.asList(SoundEvents.AMBIENT_CAVE,SoundEvents.AMBIENT_CAVE,
            SoundEvents.ENTITY_CREEPER_PRIMED,SoundEvents.ENTITY_WITHER_SKELETON_STEP,SoundEvents.BLOCK_GRASS_STEP,
            SoundEvents.BLOCK_GRASS_STEP,SoundEvents.BLOCK_GRASS_STEP,SoundEvents.ENTITY_CREEPER_PRIMED,
            SoundEvents.ENTITY_SILVERFISH_AMBIENT,SoundEvents.ENTITY_SKELETON_AMBIENT,SoundEvents.BLOCK_SAND_STEP,
            SoundEvents.BLOCK_SAND_STEP,SoundEvents.ENTITY_PLAYER_BREATH,SoundEvents.ENTITY_PLAYER_BREATH);
    public static float FOV_ADJUST = 0f;
    public static float BREATHING_FACTOR = 0f;
    public static float COLOR_CORRECTION = 0f;
    public static float AMBIENT_SOUND_CHANCE = 0f;
    public static float SCREEN_SHAKE = 0f;
    public static float QUIET_SOUNDS = 1f;
    public static float LIGHT_DIMMING = 0f;
    public static float FOG_DENSITY = 0f;
    public static float WALK_SPEED = 1f;
    public static float MINING_SPEED = 1f;
    public static float PHANTOM_VISIBILITY = 0f;

    public static float getFOVAdjustment(float fov) {
        return fov*(1f-(FOV_ADJUST/2f));
    }

    public static float getScreenShake(boolean positive) {
        float factor = SCREEN_SHAKE/2f;
        return positive ? factor : factor*-1;
    }

    public static void tryAmbientSound() {
        WorldClient world = Minecraft.getMinecraft().world;
        if(Objects.nonNull(world) && RANDOM.nextFloat()<=AMBIENT_SOUND_CHANCE/100f) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            world.playSound(player,player.posX,player.posY,player.posZ,randomSound(),SoundCategory.AMBIENT,
                    1f, MathHelper.clamp((float)(1d+doubleRand()/50d),0.9f,1.1f));
        }
    }

    private static SoundEvent randomSound() {
        return AMBIENT_SOUNDS.get(RANDOM.nextInt(AMBIENT_SOUNDS.size()));
    }

    private static double doubleRand() {
        double rand = RANDOM.nextDouble()*5d*2d;
        if(rand>5d) rand = -1d*(rand-5d);
        return rand;
    }
}