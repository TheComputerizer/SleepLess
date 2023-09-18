package mods.thecomputerizer.sleepless.world;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.util.AddedEnums;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class WorldEvents {

    @SubscribeEvent
    public static void onPlayerTrySleep(PlayerSleepInBedEvent event) {
        if(Objects.nonNull(event.getEntityPlayer().getActivePotionEffect(PotionRegistry.INSOMNIA)))
            event.setResult(AddedEnums.INSOMNIA);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP) CapabilityHandler.sync(event.player);
    }
}
