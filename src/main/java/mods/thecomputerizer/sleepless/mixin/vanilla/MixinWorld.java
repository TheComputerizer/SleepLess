package mods.thecomputerizer.sleepless.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

import static mods.thecomputerizer.sleepless.registry.PotionRegistry.PHASED;
import static org.objectweb.asm.Opcodes.GETFIELD;

@Mixin(World.class)
public abstract class MixinWorld {
    
    @Shadow @Final public List<EntityPlayer> playerEntities;
    @Shadow public abstract long getWorldTime();

    @Unique private World sleepless$cast() {
        return (World)(Object)this;
    }

    @Inject(at=@At("RETURN"),method="setWorldTime")
    private void sleepless$setWorldTime(long time, CallbackInfo info) {
        if(sleepless$cast() instanceof WorldServer && (getWorldTime()-1)%24000==23900)
            for(EntityPlayer player : this.playerEntities)
                CapabilityHandler.setTicksSlept((EntityPlayerMP)player,0,true);
    }


    @ModifyExpressionValue(at=@At(value="FIELD",target="Lnet/minecraft/entity/Entity;preventEntitySpawning:Z",
            opcode=GETFIELD),method="checkNoEntityCollision")
    private boolean sleepless$noPhantomCollisions(Entity entity, boolean original) {
        return original && (!(entity instanceof EntityLivingBase) ||
                !((EntityLivingBase)entity).isPotionActive(PHASED));
    }
}