package mods.thecomputerizer.sleepless.mixin.vanilla;

import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static mods.thecomputerizer.sleepless.util.AddedEnums.INSOMNIA;

@Mixin(BlockBed.class)
public class MixinBlockBed {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;"+
            "trySleep(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/entity/player/EntityPlayer$SleepResult;"),
            method = "onBlockActivated")
    private SleepResult sleepless$redirectSleepResult(EntityPlayer player, BlockPos pos) {
        SleepResult ret = player.trySleep(pos);
        if(ret==INSOMNIA) {
            ITextComponent msg = new TextComponentTranslation("tile.bed.sleepless.insomnia");
            player.sendStatusMessage(msg,true);
        }
        return ret;
    }
}