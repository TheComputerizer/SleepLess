package mods.thecomputerizer.sleepless.world;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.util.AddedEnums;
import mods.thecomputerizer.sleepless.world.nightterror.NightTerror;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class WorldEvents {

    private static int tickTimer=0;

    @SubscribeEvent
    public static void onPlayerTrySleep(PlayerSleepInBedEvent event) {
        if(Objects.nonNull(event.getEntityPlayer().getActivePotionEffect(PotionRegistry.INSOMNIA)))
            event.setResult(AddedEnums.INSOMNIA);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP) CapabilityHandler.sync(event.player);
    }

    @SubscribeEvent
    public static void onBreakSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        if(SleepLessConfigHelper.shouldMineSlower()) {
            EntityPlayer player = event.getEntityPlayer();
            float speedFactor = player instanceof EntityPlayerMP ?
                    CapabilityHandler.getMiningSpeedFactor((EntityPlayerMP)player) : ClientEffects.MINING_SPEED;
            event.setNewSpeed(event.getNewSpeed() * speedFactor);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if(event.phase==TickEvent.Phase.END && event.world instanceof WorldServer) {
            tickTimer++;
            if(tickTimer>20) {
                NightTerror.checkInstance((WorldServer)event.world);
                tickTimer=0;
            }
            if(Objects.nonNull(NightTerror.INSTANCE)) NightTerror.INSTANCE.onTick();
        }
    }
}
