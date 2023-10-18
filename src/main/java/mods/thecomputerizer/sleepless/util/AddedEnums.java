package mods.thecomputerizer.sleepless.util;

import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AddedEnums {

    public static final EntityPlayer.SleepResult INSOMNIA = EnumHelper.addEnum(EntityPlayer.SleepResult.class,
            "INSOMNIA",new Class<?>[]{});
    @SideOnly(Side.CLIENT)
    public static MusicTicker.MusicType NIGHT_TERROR_BEGINNING = EnumHelperClient.addMusicType("NIGHT_TERROR_BEGINNING",
            SoundRegistry.BAD_NIGHT_MUSIC,0,0);
    @SideOnly(Side.CLIENT)
    public static MusicTicker.MusicType NIGHT_TERROR_QUIET = EnumHelperClient.addMusicType("NIGHT_TERROR_QUIET",
            SoundRegistry.QUIET_MUSIC,20000,20000);

    public static void load() {}
}
