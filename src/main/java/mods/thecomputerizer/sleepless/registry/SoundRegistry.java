package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public final class SoundRegistry {

    private static final List<SoundEvent> ALL_SOUNDS = new ArrayList<>();
    public static final SoundEvent BELL_SOUND = makeSoundEvent("sounds.bell");
    public static final SoundEvent BELL_REVERSE_SOUND = makeSoundEvent("sounds.reversebell");
    public static final SoundEvent BOOSTED_TP_SOUND = makeSoundEvent("sounds.boostedtp");
    public static final SoundEvent BOOSTED_TP_REVERSE_SOUND = makeSoundEvent("sounds.boostedtpreverse");
    public static final SoundEvent STATIC_SOUND = makeSoundEvent("sounds.static");
    public static final SoundEvent BAD_NIGHT_MUSIC = makeSoundEvent("music.badnight");
    public static final SoundEvent QUIET_MUSIC = makeSoundEvent("music.quiet");

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
