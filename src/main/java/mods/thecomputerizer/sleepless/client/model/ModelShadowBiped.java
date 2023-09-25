package mods.thecomputerizer.sleepless.client.model;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.entities.PhantomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ModelShadowBiped extends ModelBiped {

    private final RenderManager manager;

    /**
     * 0 is totally black and 1 is normal
     */
    private float darkFactor;

    public ModelShadowBiped(RenderManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void render(@Nonnull Entity entity, float swing, float swingAmount, float ageInTicks, float headYaw,
                       float headPitch, float scale) {
        Constants.testLog("MODEL RENDER -0");
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Constants.testLog("IS PLAYER NONULL {} | IS INSTANCE {} | CAN I SEE YOU {}",Objects.nonNull(player),
                entity instanceof PhantomEntity,canClientSeeMe());
        if(Objects.nonNull(player) && entity instanceof PhantomEntity && canClientSeeMe()) {
            Constants.testLog("MODEL RENDER 1");
            PhantomEntity phantom = (PhantomEntity)entity;
            Render<?> shadowRender = phantom.getShadowRenderer(this.manager);
            if(Objects.nonNull(shadowRender)) {
                Constants.testLog("MODEL RENDER 2");
                if(shadowRender instanceof RenderBiped<?>) {
                    Constants.testLog("MODEL RENDER 3");
                    ModelBiped model = (ModelBiped)((RenderBiped<?>)shadowRender).getMainModel();
                    model.render(Minecraft.getMinecraft().player,swing,swingAmount,ageInTicks,headYaw,headPitch,scale);
                    Constants.testLog("MODEL RENDER 4");
                }
            }
        }
        Constants.testLog("MODEL RENDER 5");
    }

    protected boolean canClientSeeMe() {
        return ClientEffects.PHANTOM_VISIBILITY>0;
    }
}
