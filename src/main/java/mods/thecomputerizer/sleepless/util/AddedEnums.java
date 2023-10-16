package mods.thecomputerizer.sleepless.util;

import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;

public class AddedEnums {

    public static final EntityPlayer.SleepResult INSOMNIA = EnumHelper.addEnum(EntityPlayer.SleepResult.class,
            "INSOMNIA",new Class<?>[]{});
    @SideOnly(Side.CLIENT)
    public static MusicTicker.MusicType NIGHT_TERROR_MUSIC = EnumHelperClient.addMusicType("NIGHT_TERROR_MUSIC",
            SoundRegistry.BATTLE_MUSIC,0,0);

    public static void load() {

    }
}
