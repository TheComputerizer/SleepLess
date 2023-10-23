package mods.thecomputerizer.sleepless.common;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.network.PacketUpdateNightTerrorClient;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.PhantomEntity;
import mods.thecomputerizer.sleepless.util.AddedEnums;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
        if(event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.player;
            CapabilityHandler.sync(player);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        if(SleepLessConfigHelper.shouldMineSlower())
            event.setNewSpeed(event.getNewSpeed()*CapabilityHandler.getMiningSpeedFactor(event.getEntityPlayer()));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if(event.phase==TickEvent.Phase.END && event.world.provider.getDimension()==0 && event.world instanceof WorldServer) {
            WorldServer world = (WorldServer)event.world;
            tickTimer++;
            if(tickTimer>20) {
                CapabilityHandler.checkNightTerror(world);
                tickTimer=0;
            }
            CapabilityHandler.tickNightTerror(world);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
            WorldServer world = player.getServerWorld();
            if(CapabilityHandler.worldHasNightTerror(world)) CapabilityHandler.syncNightTerror(world,player);
            else new PacketUpdateNightTerrorClient(false,0f,0f,0f,-1,false)
                    .addPlayers(player).send();
        }
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if(event.getSource().getTrueSource() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.getSource().getTrueSource();
            float phantomChance = CapabilityHandler.getPhantomFactor(player);
            if(phantomChance>0f && player.world.rand.nextFloat()<=phantomChance/2f) {
                PhantomEntity.spawnPhantom(player.world,phantom -> {
                    BlockPos pos = event.getEntity().getPosition();
                    phantom.setPosition(pos.getX(),pos.getY(),pos.getZ());
                    if(phantomChance>0.5f) phantom.markAggressive();
                    phantom.setLifespan(Math.max(10,(int)(phantomChance*50f))*4);
                    phantom.tryAssignShadowClass(event.getEntity().getClass());
                });
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if(event.getSource().getTrueSource() instanceof PhantomEntity) event.getSource().getTrueSource().setDead();
    }
}
