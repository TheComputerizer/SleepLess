package mods.thecomputerizer.sleepless.mixin.vanilla;

import mods.thecomputerizer.sleepless.client.render.geometry.ModelRendererCapture;
import mods.thecomputerizer.sleepless.mixin.access.ModelRendererAccess;
import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer implements ModelRendererAccess {

    @Unique private ModelRendererCapture sleepless$capture;
    @Unique private boolean sleepless$shouldSkipCapture = false;


    @Override
    public void sleepless$setCapture(ModelRendererCapture capture) {
        this.sleepless$capture = capture;
    }

    @Override
    public ModelRendererCapture sleepless$getCapture() {
        return this.sleepless$capture;
    }

    @Override
    public void sleepless$setSkipCapture(boolean shouldSkip) {
        this.sleepless$shouldSkipCapture = shouldSkip;
    }

    @Unique private boolean sleepless$shouldCapture() {
        return !this.sleepless$shouldSkipCapture && Objects.nonNull(this.sleepless$capture);
    }

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void sleepless$renderHead(float scale, CallbackInfo ci) {
        if(sleepless$shouldCapture()) {
            this.sleepless$capture.render(scale);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderWithRotation", cancellable = true)
    private void sleepless$renderWithRotationHead(float scale, CallbackInfo ci) {
        if(sleepless$shouldCapture()) {
            this.sleepless$capture.renderWithRotation(scale);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "postRender", cancellable = true)
    private void sleepless$postRenderHead(float scale, CallbackInfo ci) {
        if(sleepless$shouldCapture()) {
            this.sleepless$capture.postRender(scale);
            ci.cancel();
        }
    }
}
