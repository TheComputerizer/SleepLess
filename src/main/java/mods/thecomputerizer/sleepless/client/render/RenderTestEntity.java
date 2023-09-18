package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.registry.entities.TestEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderTestEntity extends RenderLiving<TestEntity> {

    public RenderTestEntity(RenderManager manager, ModelBase model, float shadowSize) {
        super(manager,model,shadowSize);
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(@Nonnull TestEntity entity) {
        return null;
    }
}
