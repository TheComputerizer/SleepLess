package mods.thecomputerizer.sleepless.mixin.vanilla;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Render.class)
public interface InvokerRender<T extends Entity> {

    @Invoker ResourceLocation callGetEntityTexture(T entity);
}
