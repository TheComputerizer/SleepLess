package mods.thecomputerizer.sleepless.common;

import mods.thecomputerizer.sleepless.SleepLess;
import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.network.PacketUpdateNightTerrorClient;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomEntity;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomSpawnEntry;
import mods.thecomputerizer.sleepless.util.AddedEnums;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class WorldEvents {

    private static final double SQUARED_SPAWN_RANGE = Math.pow(144d,2d);
    public static final float MAX_PHANTOM_DESPAWN_RANGE = 16f;
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
        if(event.getSource().getTrueSource() instanceof EntityPlayerMP && !(event.getEntity() instanceof PhantomEntity)) {
            EntityPlayerMP player = (EntityPlayerMP)event.getSource().getTrueSource();
            float phantomChance = CapabilityHandler.getPhantomFactor(player);
            Random rand = player.world.rand;
            if(phantomChance>0f && SleepLess.fudgeFloat(rand.nextFloat(),0f)<=phantomChance/2f) {
                EntityLivingBase entity = event.getEntityLiving();
                PhantomEntity.spawnPhantom(player.world,entity.posX,entity.posY,entity.posZ,phantom -> {
                    if(phantomChance>0.5f && SleepLess.fudgeFloat(rand.nextFloat(),0f)<0.5f) phantom.markAggressive();
                    phantom.setLifespan(SleepLess.fudgeInt(Math.max(10,(int)(phantomChance*50f))*4,1200));
                    phantom.presetClass(event.getEntity().getClass());
                });
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if(event.getSource().getTrueSource() instanceof PhantomEntity) event.getSource().getTrueSource().setDead();
    }

    @SubscribeEvent
    public static void onGetPotentialSpawns(WorldEvent.PotentialSpawns event) {
        if(event.getType()==EnumCreatureType.MONSTER) {
            World world = event.getWorld();
            if(CapabilityHandler.worldHasNightTerror(world)) event.getList().clear();
            else {
                BlockPos pos = event.getPos();
                float phantomChance = getPhantomSpawnChance(world,pos);
                if(phantomChance>0) {
                    List<Biome.SpawnListEntry> spawnEntries = event.getList();
                    int maxGroup = Math.min(3,MathHelper.ceil(phantomChance*3f));
                    spawnEntries.add(makePhantomSpawnEntry(WeightedRandom.getTotalWeight(spawnEntries),phantomChance,maxGroup));
                }
            }
        }
    }

    private static float getPhantomSpawnChance(World world, BlockPos pos) {
        if(CapabilityHandler.worldHasNightTerror(world)) return 0f;
        float totalChance = 0f;
        int numPlayers = 0;
        for(EntityPlayer player : world.playerEntities) {
            if(player.getDistanceSq(pos)<=SQUARED_SPAWN_RANGE) {
                totalChance+=CapabilityHandler.getPhantomFactor(player);
                numPlayers++;
            }
        }
        return totalChance/numPlayers;
    }

    private static Biome.SpawnListEntry makePhantomSpawnEntry(int previousTotalWeight, float chance, int maxGroup) {
        int newWeight = (int)((float)previousTotalWeight/(1f-chance))-previousTotalWeight;
        return new PhantomSpawnEntry(PhantomEntity.class,newWeight,1,maxGroup,
                MAX_PHANTOM_DESPAWN_RANGE-(MAX_PHANTOM_DESPAWN_RANGE*chance));
    }
}
