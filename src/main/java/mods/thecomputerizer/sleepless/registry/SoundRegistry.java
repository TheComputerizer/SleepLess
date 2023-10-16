package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public final class SoundRegistry {

    private static final List<SoundEvent> ALL_SOUNDS = new ArrayList<>();
    public static final SoundEvent BELL_SOUND = makeSoundEvent("bell");
    public static final SoundEvent BOOSTED_TP_SOUND = makeSoundEvent("boostedtp");
    public static final SoundEvent BOOSTED_TP_REVERSE_SOUND = makeSoundEvent("boostedtpreverse");

    private static SoundEvent makeSoundEvent(final String name) {
        ResourceLocation id = Constants.res(name);
        SoundEvent sound = new SoundEvent(id).setRegistryName(name);
        ALL_SOUNDS.add(sound);
        return sound;
    }

    public static SoundEvent[] getSounds() {
        return ALL_SOUNDS.toArray(new SoundEvent[0]);
    }
}
