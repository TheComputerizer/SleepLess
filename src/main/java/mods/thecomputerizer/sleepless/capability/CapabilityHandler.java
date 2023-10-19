package mods.thecomputerizer.sleepless.capability;

import mods.thecomputerizer.sleepless.capability.nightterror.INightTerrorCap;
import mods.thecomputerizer.sleepless.capability.nightterror.NightTerrorCapProvider;
import mods.thecomputerizer.sleepless.capability.sleepdebt.ISleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebtProvider;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerror;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@SuppressWarnings({"DataFlowIssue", "ConstantValue"})
@Mod.EventBusSubscriber(modid = Constants.MODID)
public class CapabilityHandler {

    @CapabilityInject(ISleepDebt.class)
    public static final Capability<ISleepDebt> SLEEP_DEBT_CAPABILITY = null;
    public static final ResourceLocation SLEEP_DEBT = Constants.res("sleep_debt");

    @CapabilityInject(INightTerrorCap.class)
    public static final Capability<INightTerrorCap> NIGHT_TERROR_CAPABILITY = null;
    public static final ResourceLocation NIGHT_TERROR = Constants.res("night_terror");

    public static ISleepDebt getSleepDebtCapability(EntityPlayer player) {
        if(Objects.isNull(SLEEP_DEBT_CAPABILITY)) return null; //Probably unreachable but sometimes weird things happen
        return player.getCapability(SLEEP_DEBT_CAPABILITY,null);
    }

    public static INightTerrorCap getNightTerrorCapability(World world) {
        if(Objects.isNull(NIGHT_TERROR_CAPABILITY)) return null; //Probably unreachable but sometimes weird things happen
        return world instanceof WorldServer ? world.getCapability(NIGHT_TERROR_CAPABILITY,null) : null;
    }

    public static void setSleepDebt(EntityPlayerMP player, float debt) {
        getSleepDebtCapability(player).setDebt(player,debt);
    }

    /**
     * The more abstracted input makes some other checks require less syntax sugar
     */
    public static float getSleepDebt(Entity potentialPlayer) {
        if(!(potentialPlayer instanceof EntityPlayerMP)) return -1f;
        ISleepDebt cap = getSleepDebtCapability((EntityPlayerMP)potentialPlayer);
        return Objects.nonNull(cap) ? cap.getDebt() : -1f;
    }

    public static float getHungerAmplifier(EntityPlayerMP player, float exhaustion) {
        return exhaustion*(1f+getSleepDebtCapability(player).getHungerAmplifier());
    }

    public static float getMiningSpeedFactor(EntityPlayerMP player) {
        return getSleepDebtCapability(player).getMiningSpeedFactor();
    }

    public static void setTicksSlept(EntityPlayerMP player, long ticks, boolean notifyPlayer) {
        ISleepDebt cap = getSleepDebtCapability(player);
        if(cap.onTicksSlept(ticks)) {
            sync(player);
            if(notifyPlayer)
                player.sendStatusMessage(new TextComponentTranslation("status.sleepless.sleepdebt",
                        CapabilityHandler.getSleepDebt(player)), true);
        }
        checkTiredEffect(player,(int)cap.getDebt());
    }

    private static void checkTiredEffect(EntityPlayer player, int level) {
        Potion tired = PotionRegistry.TIRED;
        PotionEffect tiredEffect = player.getActivePotionEffect(tired);
        if(level<1) {
            if(Objects.nonNull(tiredEffect)) player.removePotionEffect(tired);
        } else {
            if(Objects.isNull(tiredEffect))
                player.addPotionEffect(new PotionEffect(tired,Integer.MAX_VALUE,level));
            else tiredEffect.amplifier = level;
        }
    }

    public static void sync(EntityPlayer player) {
        getSleepDebtCapability(player).sync((EntityPlayerMP)player);
    }

    public static void setNewNightTerror(WorldServer world) {
        INightTerrorCap cap = getNightTerrorCapability(world);
        if(Objects.nonNull(cap) && Objects.isNull(cap.getInstance()))
            cap.setInstance(new NightTerror(world));
    }

    public static boolean worldHasNightTerror(World world) {
        INightTerrorCap cap = getNightTerrorCapability(world);
        return Objects.nonNull(cap) && Objects.nonNull(cap.getInstance());
    }

    public static void checkNightTerror(WorldServer world) {
        INightTerrorCap cap = getNightTerrorCapability(world);
        if(Objects.nonNull(cap)) cap.checkInstance(world);
    }

    public static void tickNightTerror(World world) {
        INightTerrorCap cap = getNightTerrorCapability(world);
        if(Objects.nonNull(cap) && Objects.nonNull(cap.getInstance())) cap.getInstance().onTick();
    }

    public static void syncNightTerror(World world, EntityPlayerMP player) {
        getNightTerrorCapability(world).onPlayerJoinWorld(player);
    }

    public static boolean shouldDaylightCycle(World world) {
        INightTerrorCap cap = getNightTerrorCapability(world);
        return Objects.nonNull(cap) && cap.shoudlDaylightCycle();
    }

    public static boolean finishNightTerror(World world) {
        INightTerrorCap cap = getNightTerrorCapability(world);
        if(Objects.nonNull(cap) && Objects.nonNull(cap.getInstance())) {
            cap.finish();
            cap.setInstance(null);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if(entity instanceof EntityPlayerMP) event.addCapability(SLEEP_DEBT,new SleepDebtProvider());
    }

    @SubscribeEvent
    public static void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> event) {
        World world = event.getObject();
        if(world instanceof WorldServer) event.addCapability(NIGHT_TERROR,new NightTerrorCapProvider());
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if(event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP to = (EntityPlayerMP) event.getEntityPlayer();
            ISleepDebt capTo = CapabilityHandler.getSleepDebtCapability(to);
            if(Objects.nonNull(capTo)) {
                EntityPlayerMP from = (EntityPlayerMP) event.getOriginal();
                ISleepDebt capFrom = CapabilityHandler.getSleepDebtCapability(from);
                if(Objects.nonNull(capFrom)) capTo.of(to,(SleepDebt)capFrom);
            }
        }
    }
}
