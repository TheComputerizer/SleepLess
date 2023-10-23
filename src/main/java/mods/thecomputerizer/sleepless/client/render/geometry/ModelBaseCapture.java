package mods.thecomputerizer.sleepless.client.render.geometry;

import mods.thecomputerizer.sleepless.mixin.access.ModelRendererAccess;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
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
                    29d/32d : 28d/32d;
            ((ModelRendererAccess)renderer).sleepless$setCapture(new ModelRendererCapture(renderer,29d/32d));
        }
        this.isTransformed = true;
    }

    public void setVisibility(boolean isVisible) {
        if(!this.isTransformed) transformRenderers();
        for(ModelRenderer renderer : this.parentModel.boxList)
            ((ModelRendererAccess)renderer).sleepless$getCapture().setVisible(isVisible);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTick) {
        if(!(entity instanceof NightTerrorEntity) || ((NightTerrorEntity)entity).getAnimationData().currentAnimation==NightTerrorEntity.AnimationType.IDLE)
            this.parentModel.setLivingAnimations(entity,limbSwing,limbSwingAmount,partialTick);
    }
}
