package mods.thecomputerizer.sleepless.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.EnumHelper;

public class AddedEnums {

    public static final EntityPlayer.SleepResult INSOMNIA = EnumHelper.addEnum(EntityPlayer.SleepResult.class,
            "INSOMNIA",new Class<?>[]{});

    public static void load() {}
}
