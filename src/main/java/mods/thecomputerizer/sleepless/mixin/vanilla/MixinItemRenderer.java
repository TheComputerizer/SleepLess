package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.registry.items.TesseractItem;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.util.math.Vec3d.ZERO;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Inject(at=@At("HEAD"),method="renderItem",cancellable=true)
    private void sleepless$renderItem(EntityLivingBase entity, ItemStack stack, TransformType type, CallbackInfo ci) {
        if(stack.getItem() instanceof TesseractItem) {
            ((TesseractItem)stack.getItem()).getRenderer().render(ZERO);
            ci.cancel();
        }
    }

    @Inject(at=@At("HEAD"),method="renderItemSide",cancellable=true)
    private void sleepless$renderItemSide(EntityLivingBase entity, ItemStack stack, TransformType type, boolean b,
            CallbackInfo ci) {
        if(stack.getItem() instanceof TesseractItem) {
            ((TesseractItem)stack.getItem()).getRenderer().render(ZERO);
            ci.cancel();
        }
    }

    @Inject(at=@At("HEAD"),method="renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;"+
            "FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",cancellable=true)
    private void sleepless$renderItemInFirstPerson(AbstractClientPlayer player, float f1, float f2, EnumHand hand,
            float f3, ItemStack stack, float f4, CallbackInfo ci) {
        if(stack.getItem() instanceof TesseractItem) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(1f,-0.5f,-1.5f);
            ((TesseractItem)stack.getItem()).getRenderer().render(ZERO);
            GlStateManager.popMatrix();
            ci.cancel();
        }
    }
}