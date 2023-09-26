package mods.thecomputerizer.sleepless.client.model;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.registry.entities.PhantomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ModelShadowBiped extends ModelBiped {

    private final RenderManager manager;

    /**
    *0 is totally black and 1 is normal
     */
    private float darkFactor;

    public ModelShadowBiped(RenderManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void render(@Nonnull Entity entity, float swing, float swingAmount, float ageInTicks, float headYaw,
                       float headPitch, float scale) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(Objects.nonNull(player) && entity instanceof PhantomEntity && canClientSeeMe()) {
            PhantomEntity phantom = (PhantomEntity)entity;
            Render<?> shadowRender = phantom.getShadowRenderer(this.manager);
            if(shadowRender instanceof RenderBiped<?>) {
                ModelBiped model = (ModelBiped)((RenderBiped<?>)shadowRender).getMainModel();
                renderAsBiped(entity,model,swing,swingAmount,ageInTicks,headYaw,headPitch,scale);
            } else if(shadowRender instanceof RenderPlayer) {
                ModelPlayer model = ((RenderPlayer)shadowRender).getMainModel();
                renderAsPlayer(entity,model,swing,swingAmount,ageInTicks,headYaw,headPitch,scale);
            }
        }
    }

    protected void renderAsBiped(@Nonnull Entity entity, @Nonnull ModelBiped model, float swing, float swingAmount,
                               float ageInTicks, float headYaw, float headPitch, float scale) {
        this.setRotationAngles(entity,model,swing,swingAmount,ageInTicks,headYaw,headPitch);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            GlStateManager.scale(0.75f,0.75f,0.75f);
            GlStateManager.translate(0f,16f*scale,0f);
            model.bipedHead.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f,0.5f,0.5f);
            GlStateManager.translate(0f,24f*scale,0f);
        }
        else {
            if(entity.isSneaking()) GlStateManager.translate(0f,0.2f,0f);
            model.bipedHead.render(scale);
        }
        model.bipedBody.render(scale);
        model.bipedRightArm.render(scale);
        model.bipedLeftArm.render(scale);
        model.bipedRightLeg.render(scale);
        model.bipedLeftLeg.render(scale);
        model.bipedHeadwear.render(scale);
        GlStateManager.popMatrix();
    }

    protected void renderAsPlayer(@Nonnull Entity entity, @Nonnull ModelPlayer model, float swing, float swingAmount,
                                float ageInTicks, float headYaw, float headPitch, float scale) {
        renderAsBiped(entity,model,swing,swingAmount,ageInTicks,headYaw,headPitch,scale);
        GlStateManager.pushMatrix();
        if(this.isChild) {
            GlStateManager.scale(0.5f,0.5f,0.5f);
            GlStateManager.translate(0f,24f*scale,0f);
        }
        else if(entity.isSneaking()) GlStateManager.translate(0f,0.2f,0f);
        model.bipedLeftLegwear.render(scale);
        model.bipedRightLegwear.render(scale);
        model.bipedLeftArmwear.render(scale);
        model.bipedRightArmwear.render(scale);
        model.bipedBodyWear.render(scale);
        GlStateManager.popMatrix();
    }

    protected void setRotationAngles(@Nonnull Entity entity, @Nonnull ModelBiped model, float swing, float swingAmount,
                                     float ageInTicks, float headYaw, float headPitch) {
        if(model instanceof ModelPlayer)
            setPlayerRotationAngles(entity,(ModelPlayer)model);
        boolean flag = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getTicksElytraFlying() > 4;
        model.bipedHead.rotateAngleY = headYaw*0.017453292f;
        model.bipedHead.rotateAngleX = flag ? -((float)Math.PI/4f) : headPitch*0.017453292f;
        model.bipedBody.rotateAngleY = 0f;
        model.bipedRightArm.rotationPointZ = 0f;
        model.bipedRightArm.rotationPointX = -5f;
        model.bipedLeftArm.rotationPointZ = 0f;
        model.bipedLeftArm.rotationPointX = 5f;
        float motionFactor = 1f;
        if (flag) {
            double d = (entity.motionX*entity.motionX)+(entity.motionY*entity.motionY)+(entity.motionZ*entity.motionZ);
            motionFactor = Math.max((float)Math.pow(d/0.2d,3d),1f);
        }
        model.bipedRightArm.rotateAngleX = MathHelper.cos(swing*0.6662f+(float)Math.PI)*2f*swingAmount*0.5f/motionFactor;
        model.bipedLeftArm.rotateAngleX = MathHelper.cos(swing*0.6662f)*2f*swingAmount*0.5f/motionFactor;
        model.bipedLeftArm.rotateAngleZ = 0f;
        model.bipedRightLeg.rotateAngleX = MathHelper.cos(swing*0.6662f)*1.4f*swingAmount/motionFactor;
        model.bipedLeftLeg.rotateAngleX = MathHelper.cos(swing*0.6662f+(float)Math.PI)*1.4f*swingAmount/motionFactor;
        model.bipedRightLeg.rotateAngleY = 0f;
        model.bipedLeftLeg.rotateAngleY = 0f;
        model.bipedRightLeg.rotateAngleZ = 0f;
        model.bipedLeftLeg.rotateAngleZ = 0f;
        if(model.isRiding) {
            model.bipedRightArm.rotateAngleX -= ((float) Math.PI/5f);
            model.bipedLeftArm.rotateAngleX -= ((float) Math.PI/5f);
            model.bipedRightLeg.rotateAngleX = -1.4137167f;
            model.bipedRightLeg.rotateAngleY = ((float)Math.PI/10f);
            model.bipedRightLeg.rotateAngleZ = 0.07853982f;
            model.bipedLeftLeg.rotateAngleX = -1.4137167f;
            model.bipedLeftLeg.rotateAngleY = -((float)Math.PI/10f);
            model.bipedLeftLeg.rotateAngleZ = -0.07853982f;
        }
        model.bipedRightArm.rotateAngleY = 0f;
        model.bipedRightArm.rotateAngleZ = 0f;
        switch(this.leftArmPose) {
            case EMPTY:
                model.bipedLeftArm.rotateAngleY = 0f;
                break;
            case BLOCK:
                model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX*0.5f-0.9424779f;
                model.bipedLeftArm.rotateAngleY = 0.5235988f;
                break;
            case ITEM:
                model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX*0.5f-((float)Math.PI/10f);
                model.bipedLeftArm.rotateAngleY = 0f;
        }

        switch(this.rightArmPose) {
            case EMPTY:
                model.bipedRightArm.rotateAngleY = 0f;
                break;
            case BLOCK:
                model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX*0.5f-0.9424779f;
                model.bipedRightArm.rotateAngleY = -0.5235988f;
                break;
            case ITEM:
                model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX*0.5f-((float)Math.PI/10f);
                model.bipedRightArm.rotateAngleY = 0f;
        }

        if(this.swingProgress>0f) {
            EnumHandSide enumhandside = model.getMainHand(entity);
            ModelRenderer modelrenderer = model.getArmForSide(enumhandside);
            float f1 = this.swingProgress;
            model.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1)*((float)Math.PI*2f))*0.2f;
            if(enumhandside == EnumHandSide.LEFT) model.bipedBody.rotateAngleY *= -1f;
            model.bipedRightArm.rotationPointZ = MathHelper.sin(model.bipedBody.rotateAngleY)*5f;
            model.bipedRightArm.rotationPointX = -MathHelper.cos(model.bipedBody.rotateAngleY)*5f;
            model.bipedLeftArm.rotationPointZ = -MathHelper.sin(model.bipedBody.rotateAngleY)*5f;
            model.bipedLeftArm.rotationPointX = MathHelper.cos(model.bipedBody.rotateAngleY)*5f;
            model.bipedRightArm.rotateAngleY += model.bipedBody.rotateAngleY;
            model.bipedLeftArm.rotateAngleY += model.bipedBody.rotateAngleY;
            //noinspection SuspiciousNameCombination
            model.bipedLeftArm.rotateAngleX += model.bipedBody.rotateAngleY; // Is rotateAngleY supposed to be added to rotateAngleX?
            f1 = 1f-this.swingProgress;
            f1 = f1*f1;
            f1 = f1*f1;
            f1 = 1f-f1;
            float f2 = MathHelper.sin(f1*(float)Math.PI);
            float f3 = MathHelper.sin(this.swingProgress*(float)Math.PI)*-(model.bipedHead.rotateAngleX-0.7f)*0.75f;
            modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX-((double)f2*1.2d+(double)f3));
            modelrenderer.rotateAngleY += model.bipedBody.rotateAngleY*2f;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress*(float)Math.PI)*-0.4f;
        }
        if(model.isSneak) {
            model.bipedBody.rotateAngleX = 0.5f;
            model.bipedRightArm.rotateAngleX += 0.4f;
            model.bipedLeftArm.rotateAngleX += 0.4f;
            model.bipedRightLeg.rotationPointZ = 4.0f;
            model.bipedLeftLeg.rotationPointZ = 4.0f;
            model.bipedRightLeg.rotationPointY = 9.0f;
            model.bipedLeftLeg.rotationPointY = 9.0f;
            model.bipedHead.rotationPointY = 1f;
        }
        else {
            model.bipedBody.rotateAngleX = 0f;
            model.bipedRightLeg.rotationPointZ = 0.1f;
            model.bipedLeftLeg.rotationPointZ = 0.1f;
            model.bipedRightLeg.rotationPointY = 12f;
            model.bipedLeftLeg.rotationPointY = 12f;
            model.bipedHead.rotationPointY = 0f;
        }
        model.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks*0.09f)*0.05f+0.05f;
        model.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks*0.09f)*0.05f+0.05f;
        model.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks*0.067f)*0.05f;
        model.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks*0.067f)*0.05f;
        if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            model.bipedRightArm.rotateAngleY = -0.1f+model.bipedHead.rotateAngleY;
            model.bipedLeftArm.rotateAngleY = 0.1f+model.bipedHead.rotateAngleY+0.4f;
            model.bipedRightArm.rotateAngleX = -((float)Math.PI/2F)+model.bipedHead.rotateAngleX;
            model.bipedLeftArm.rotateAngleX = -((float)Math.PI/2F)+model.bipedHead.rotateAngleX;
        }
        else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            model.bipedRightArm.rotateAngleY = -0.1f+model.bipedHead.rotateAngleY-0.4f;
            model.bipedLeftArm.rotateAngleY = 0.1f+model.bipedHead.rotateAngleY;
            model.bipedRightArm.rotateAngleX = -((float)Math.PI/2F)+model.bipedHead.rotateAngleX;
            model.bipedLeftArm.rotateAngleX = -((float)Math.PI/2F)+model.bipedHead.rotateAngleX;
        }
        copyModelAngles(model.bipedHead, model.bipedHeadwear);
    }

    private void setPlayerRotationAngles(@Nonnull Entity entity, @Nonnull ModelPlayer model) {
        copyModelAngles(model.bipedLeftLeg,model.bipedLeftLegwear);
        copyModelAngles(model.bipedRightLeg,model.bipedRightLegwear);
        copyModelAngles(model.bipedLeftArm,model.bipedLeftArmwear);
        copyModelAngles(model.bipedRightArm,model.bipedRightArmwear);
        copyModelAngles(model.bipedBody,model.bipedBodyWear);
        model.bipedCape.rotationPointY = entity.isSneaking() ? 2f : 0f;
    }

    protected boolean canClientSeeMe() {
        return ClientEffects.PHANTOM_VISIBILITY>0;
    }
}
