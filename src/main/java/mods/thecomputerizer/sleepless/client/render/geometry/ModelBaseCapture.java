package mods.thecomputerizer.sleepless.client.render.geometry;

import mods.thecomputerizer.sleepless.mixin.access.ModelRendererAccess;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModelBaseCapture extends ModelBase {

    private final ModelBase parentModel;
    private boolean isTransformed = false;

    public ModelBaseCapture(ModelBase parentModel) {
        this.parentModel = parentModel;
    }

    public ModelBase getParentModel() {
        return this.parentModel;
    }

    @Override
    public void render(Entity entity, float swing, float swingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {
        if(!this.isTransformed) transformRenderers();
        this.parentModel.render(entity,swing,swingAmount,ageInTicks,headYaw,headPitch,scale);
    }

    private void transformRenderers() {
        for(ModelRenderer renderer : this.parentModel.boxList) {
            double scale = this.parentModel instanceof ModelBiped && ((ModelBiped)this.parentModel).bipedBody==renderer ?
                    29d/32d : 27d/32d;
            ((ModelRendererAccess)renderer).sleepless$setCapture(new ModelRendererCapture(renderer,scale));
        }
        this.isTransformed = true;
    }

    public void setVisibility(boolean isVisible) {
        if(!this.isTransformed) transformRenderers();
        for(ModelRenderer renderer : this.parentModel.boxList)
            ((ModelRendererAccess)renderer).sleepless$getCapture().setVisible(isVisible);
    }
}
