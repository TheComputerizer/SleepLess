package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.util.AddedEnums;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockBed.class)
public class MixinBlockBed {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;"+
            "trySleep(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/entity/player/EntityPlayer$SleepResult;"),
            method = "onBlockActivated")
    private EntityPlayer.SleepResult sleepless$redirectSleepResult(EntityPlayer player, BlockPos pos) {
        EntityPlayer.SleepResult ret = player.trySleep(pos);
        if(ret==AddedEnums.INSOMNIA) player.sendStatusMessage(new TextComponentTranslation("tile.bed.sleepless.insomnia"), true);
        return ret;
    }
}
