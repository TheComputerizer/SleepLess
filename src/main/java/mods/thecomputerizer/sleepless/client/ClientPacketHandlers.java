package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

/**
 * The class itself shouldn't be annotated with SideOnly but all methods and parameters should be
 */
public class ClientPacketHandlers {

    @SideOnly(Side.CLIENT)
    public static void updateClientEffects(float grayscale, float ambientChance, float quietSounds, float lightDim) {
        ClientEffects.COLOR_CORRECTION = grayscale;
        ClientEffects.AMBIENT_SOUND_CHANCE = ambientChance;
        ClientEffects.QUIET_SOUNDS = quietSounds;
        ClientEffects.LIGHT_DIMMING = lightDim;
        Constants.testLog("SET GRAYSCALE TO {} AMBIENT TO {} QUIET TO {} AND LIGHT DIM TO {}",grayscale,ambientChance,quietSounds,lightDim);
        Minecraft mc = Minecraft.getMinecraft();
        ((SoundSystem)mc.getSoundHandler().sndManager.sndSystem).setMasterVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
    }
}
