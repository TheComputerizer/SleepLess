package mods.thecomputerizer.sleepless.world;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.util.AddedEnums;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class WorldEvents {

    @SubscribeEvent
    public static void onPlayerTrySleep(PlayerSleepInBedEvent event) {
        if(Objects.nonNull(event.getEntityPlayer().getActivePotionEffect(PotionRegistry.INSOMNIA)))
            event.setResult(AddedEnums.INSOMNIA);
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if(event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
            player.sendStatusMessage(new TextComponentTranslation("tile.bed.sleepless.wakeup",
                    CapabilityHandler.getSleepDebt(player)), true);
        }
    }
}
