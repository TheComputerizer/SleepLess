package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

import java.util.Objects;

/**
 * The class itself shouldn't be annotated with SideOnly but all methods and parameters should be
 */
public class ClientPacketHandlers {

    @SideOnly(Side.CLIENT)
    public static void updateClientEffects(float grayscale, float ambientChance, float quietSounds, float lightDim,
                                           float fogDensity, float walkSpeed, float breathingFactor, float miningSpeed) {
        ClientEffects.COLOR_CORRECTION = grayscale;
        ClientEffects.AMBIENT_SOUND_CHANCE = ambientChance;
        ClientEffects.QUIET_SOUNDS = quietSounds;
        ClientEffects.LIGHT_DIMMING = lightDim;
        ClientEffects.FOG_DENSITY = fogDensity;
        ClientEffects.WALK_SPEED = walkSpeed*0.1f;
        ClientEffects.BREATHING_FACTOR = breathingFactor;
        ClientEffects.MINING_SPEED = miningSpeed;
        Constants.testLog("SET GRAYSCALE TO {} AMBIENT TO {} QUIET TO {} AND LIGHT DIM TO {}",grayscale,ambientChance,quietSounds,lightDim);
        Minecraft mc = Minecraft.getMinecraft();
        ((SoundSystem)mc.getSoundHandler().sndManager.sndSystem).setMasterVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
        if(Objects.nonNull(mc.player)) mc.player.capabilities.setPlayerWalkSpeed(ClientEffects.WALK_SPEED);
        if(ClientEffects.BREATHING_FACTOR==0) ClientEffects.FOV_ADJUST = 0f;
    }
}
