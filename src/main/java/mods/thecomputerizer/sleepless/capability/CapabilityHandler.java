package mods.thecomputerizer.sleepless.capability;

import mods.thecomputerizer.sleepless.capability.sleepdebt.ISleepDebt;
import mods.thecomputerizer.sleepless.capability.sleepdebt.SleepDebtProvider;
import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("DataFlowIssue")
@Mod.EventBusSubscriber(modid = Constants.MODID)
public class CapabilityHandler {

    @CapabilityInject(ISleepDebt.class)
    public static final Capability<ISleepDebt> SLEEP_DEBT_CAPABILITY = null;
    public static final ResourceLocation SLEEP_DEBT = Constants.res("sleep_debt");

    public static ISleepDebt getSleepDebtCapability(EntityPlayer player) {
        return player.getCapability(SLEEP_DEBT_CAPABILITY,null);
    }

    public static void setTicksSlept(EntityPlayer player, long ticks) {
        getSleepDebtCapability(player).onTicksSlept(ticks);
    }

    public static float getSleepDebt(EntityPlayer player) {
        return getSleepDebtCapability(player).getDebt();
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if(entity instanceof EntityPlayerMP)
            event.addCapability(SLEEP_DEBT,new SleepDebtProvider());
    }
}
