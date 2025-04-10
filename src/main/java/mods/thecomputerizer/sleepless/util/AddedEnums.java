package mods.thecomputerizer.sleepless.util;

import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mods.thecomputerizer.sleepless.registry.SoundRegistry.BAD_NIGHT_MUSIC;
import static mods.thecomputerizer.sleepless.registry.SoundRegistry.EERIE_MUSIC;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class AddedEnums {

    public static final EntityPlayer.SleepResult INSOMNIA = EnumHelper.addEnum(EntityPlayer.SleepResult.class,
            "INSOMNIA",new Class<?>[]{});
    
    @SideOnly(CLIENT) public static MusicType NIGHT_TERROR_BEGINNING =
            EnumHelperClient.addMusicType("NIGHT_TERROR_BEGINNING",BAD_NIGHT_MUSIC,0,0);
    @SideOnly(CLIENT) public static MusicType NIGHT_TERROR_EERIE =
            EnumHelperClient.addMusicType("NIGHT_TERROR_EERIE",EERIE_MUSIC,0,0);

    public static void load() {}
}