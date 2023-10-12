package mods.thecomputerizer.sleepless.capability;

import mods.thecomputerizer.sleepless.capability.sleepdebt.ISleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebtProvider;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
@Mod.EventBusSubscriber(modid = Constants.MODID)
public class CapabilityHandler {

    @CapabilityInject(ISleepDebt.class)
    public static final Capability<ISleepDebt> SLEEP_DEBT_CAPABILITY = null;
    public static final ResourceLocation SLEEP_DEBT = Constants.res("sleep_debt");

    public static ISleepDebt getSleepDebtCapability(EntityPlayer player) {
        return player.getCapability(SLEEP_DEBT_CAPABILITY,null);
    }

    public static void setSleepDebt(EntityPlayerMP player, float debt) {
        getSleepDebtCapability(player).setDebt(player,debt);
    }

    public static float getSleepDebt(EntityPlayerMP player) {
        return getSleepDebtCapability(player).getDebt();
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

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if(entity instanceof EntityPlayerMP)
            event.addCapability(SLEEP_DEBT,new SleepDebtProvider());
    }
}
