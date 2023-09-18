package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.core.Constants;
import org.spongepowered.asm.mixin.Mixin;
import paulscode.sound.SoundSystem;

@Mixin(targets = "net.minecraft.client.audio.SoundManager$SoundSystemStarterThread")
public abstract class MixinSoundSystemStarterThread extends SoundSystem {

    @Override
    public void setMasterVolume(float volume) {
        Constants.testLog("SETTING MASTER VOLUME TO {} INSTEAD LMAO",volume*ClientEffects.QUIET_SOUNDS);
        super.setMasterVolume(volume*ClientEffects.QUIET_SOUNDS);
    }
}
