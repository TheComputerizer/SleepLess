package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

@Mixin(value = NetHandlerPlayServer.class, remap = false)
public class MixinNetHandlerPlayServer {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getCollisionBoxes(" +
            "Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", ordinal = 1),
            method = "processPlayer")
    private List<AxisAlignedBB> sleepless$specialNoClip(WorldServer world, Entity entity, AxisAlignedBB aabb) {
        if(entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(PotionRegistry.PHASED))
            return Collections.emptyList();
        return world.getCollisionBoxes(entity,aabb);
    }
}
