package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow public abstract long getWorldTime();

    @Shadow @Final public List<EntityPlayer> playerEntities;

    @Unique private World sleepless$cast() {
        return (World)(Object)this;
    }

    @Inject(at = @At("RETURN"), method = "setWorldTime")
    private void sleepless$setWorldTime(long time, CallbackInfo info) {
        if(sleepless$cast() instanceof WorldServer && (getWorldTime()-1)%24000==23900)
            for(EntityPlayer player : this.playerEntities)
                CapabilityHandler.setTicksSlept(player,0);
    }
}
