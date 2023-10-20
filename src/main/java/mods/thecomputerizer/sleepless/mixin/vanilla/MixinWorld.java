package mods.thecomputerizer.sleepless.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

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
                CapabilityHandler.setTicksSlept((EntityPlayerMP)player,0,true);
    }


    @SuppressWarnings("unused")
    @ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;preventEntitySpawning:Z",
            opcode = Opcodes.GETFIELD), method = "checkNoEntityCollision")
    private boolean sleepless$noPhantomCollisions(Entity entity, boolean original) {
        return original && (!(entity instanceof EntityLivingBase) ||
                Objects.isNull(((EntityLivingBase)entity).getActivePotionEffect(PotionRegistry.PHASED)));
    }
}
