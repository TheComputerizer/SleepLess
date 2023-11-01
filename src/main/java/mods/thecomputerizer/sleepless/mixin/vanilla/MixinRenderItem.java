package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.registry.items.TesseractItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(RenderItem.class)
public class MixinRenderItem {

    @Inject(at = @At("HEAD"), method = "renderItemAndEffectIntoGUI(Lnet/minecraft/entity/EntityLivingBase;" +
            "Lnet/minecraft/item/ItemStack;II)V", cancellable = true)
    private void sleepless$renderItemAndEffectIntoGUI(@Nullable EntityLivingBase entity, final ItemStack stack, int x,
                                                      int y, CallbackInfo ci) {
        if(stack.getItem() instanceof TesseractItem) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(36f,36f,36f);
            ((TesseractItem)stack.getItem()).getRenderer().render(new Vec3d((double)(x+8)/36d,(double)(y+8)/36d,0d));
            GlStateManager.popMatrix();
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderItem(Lnet/minecraft/item/ItemStack;" +
            "Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", cancellable = true)
    private void sleepless$renderItem(ItemStack stack, IBakedModel model, CallbackInfo ci) {
        if(stack.getItem() instanceof TesseractItem) {
            ((TesseractItem)stack.getItem()).getRenderer().render(Vec3d.ZERO);
            ci.cancel();
        }
    }
}
