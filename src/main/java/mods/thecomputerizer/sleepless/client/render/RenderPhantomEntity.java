package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.client.model.ModelShadowBiped;
import mods.thecomputerizer.sleepless.mixin.vanilla.InvokerRender;
import mods.thecomputerizer.sleepless.registry.entities.PhantomEntity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class RenderPhantomEntity extends RenderBiped<PhantomEntity> {

    public RenderPhantomEntity(RenderManager manager) {
        super(manager,new ModelShadowBiped(manager),1f);
    }

    @Override
    public void doRender(@Nonnull PhantomEntity entity, double x, double y, double z, float yaw, float partialTicks) {
        if(entity.isInitialized()) super.doRender(entity,x,y,z,yaw,partialTicks);
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(@Nonnull PhantomEntity entity) {
        Render<?> render = entity.getShadowRenderer(this.renderManager);
        return Objects.nonNull(render) ? ((InvokerRender<?>)render).callGetEntityTexture(null) : null;
    }
}
