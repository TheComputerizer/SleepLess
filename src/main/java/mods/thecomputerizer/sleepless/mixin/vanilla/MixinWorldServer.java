package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomSpawnEntry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(at = @At("HEAD"), method = "canCreatureTypeSpawnHere", cancellable = true)
    private void sleepless$canCreatureTypeSpawnHere(EnumCreatureType type, Biome.SpawnListEntry entry, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(entry instanceof PhantomSpawnEntry) cir.setReturnValue(true);
    }
}
