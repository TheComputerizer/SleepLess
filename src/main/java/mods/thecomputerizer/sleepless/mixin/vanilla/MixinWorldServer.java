package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class)
public class MixinWorldServer {

    @Unique private WorldServer sleepless$cast() {
        return (WorldServer)(Object)this;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getWorldTime()J", ordinal = 0),
            method = "tick")
    private long sleepless$redirectGetWorldTime(WorldServer world) {
        long time = world.getWorldTime();
        long next = time+24000L;
        long ticksPassed = (next-next%24000L)-time;
        for(EntityPlayer player : world.playerEntities)
            CapabilityHandler.setTicksSlept((EntityPlayerMP)player,ticksPassed,true);
        return time;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;" +
            "getBoolean(Ljava/lang/String;)Z", ordinal = 0), method = "tick")
    private boolean sleepless$redirectDoDaylightCycle1(GameRules instance, String name) {
        return instance.getBoolean(name) && CapabilityHandler.shouldDaylightCycle(sleepless$cast());
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;" +
            "getBoolean(Ljava/lang/String;)Z", ordinal = 2), method = "tick")
    private boolean sleepless$redirectDoDaylightCycle2(GameRules instance, String name) {
        return instance.getBoolean(name) && CapabilityHandler.shouldDaylightCycle(sleepless$cast());
    }
}
