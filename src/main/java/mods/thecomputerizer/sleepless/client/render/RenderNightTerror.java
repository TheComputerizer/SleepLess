package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.client.render.geometry.ModelBaseCapture;
import mods.thecomputerizer.sleepless.client.render.geometry.ModelRendererCapture;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.mixin.access.ModelRendererAccess;
import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class RenderNightTerror extends RenderLivingBase<NightTerrorEntity> {

    private static final ResourceLocation SKIN_TEXTURE = Constants.res("textures/entity/night_terror.png");

    private float animationAngle = 0f;
    private Vec3d animationAngleScales = Vec3d.ZERO;

    public RenderNightTerror(RenderManager manager) {
        super(manager,new ModelBaseCapture(new ModelPlayer(0f,true)),0f);
        ModelPlayer playerModel = getPlayerModel();
        playerModel.isChild = false;
        setPartAnimations(playerModel.bipedRightLeg,anim -> anim.setMirrored(true),true);
        setPartAnimations(playerModel.bipedRightArm,anim -> anim.setMirrored(true),true);
    }

    private ModelPlayer getPlayerModel() {
        return ((ModelPlayer)((ModelBaseCapture)this.getMainModel()).getParentModel());
    }

    @Override
    public void doRender(NightTerrorEntity entity, double x, double y, double z, float entityYaw, float partialTick) {
        if(entity.renderMode<0 || entity.renderMode>2) return;
        applyCustomAnimations(entity,partialTick);
        if(entity.renderMode==0) entity.getAnimationData().renderAlt(new Vec3d(x,y+1.5d,z));
        else {
            ((ModelBaseCapture)getMainModel()).setVisibility(entity.renderMode==1);
            double sneakOffset = entity.isSneaking() ? y-0.125d : y;
            this.setModelVisibilities(entity);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            GlStateManager.disableLighting();
            super.doRender(entity,x,sneakOffset,z,entityYaw, partialTick);
            GlStateManager.enableLighting();
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }

    private void setPartAnimations(ModelRenderer part, Consumer<ModelRendererCapture.ModelPartAnimation> animationFunc,
                                   boolean createNew) {
        ModelRendererCapture capture = ((ModelRendererAccess)part).sleepless$getCapture();
        if(Objects.nonNull(capture)) {
            if(Objects.isNull(capture.additionalAnimations) && createNew)
                capture.additionalAnimations = new ModelRendererCapture.ModelPartAnimation();
            if(Objects.nonNull(capture.additionalAnimations)) animationFunc.accept(capture.additionalAnimations);
        }
    }

    private void resetPartAnimations() {
        for(ModelRenderer part : getMainModel().boxList)
            setPartAnimations(part,ModelRendererCapture.ModelPartAnimation::reset,false);
    }

    private void setLimbAnimations(Consumer<ModelRendererCapture.ModelPartAnimation> armAnimations,
                                   Consumer<ModelRendererCapture.ModelPartAnimation> legAnimations) {
        ModelPlayer playerModel = getPlayerModel();
        setPartAnimations(playerModel.bipedLeftArm,armAnimations,true);
        setPartAnimations(playerModel.bipedRightArm,armAnimations,true);
        setPartAnimations(playerModel.bipedLeftLeg,legAnimations,true);
        setPartAnimations(playerModel.bipedRightLeg,legAnimations,true);
    }

    private void applyCustomAnimations(NightTerrorEntity entity, float partialTick) {
        NightTerrorEntity.AnimationData data = entity.getAnimationData();
        switch(data.currentAnimation) {
            case SPAWN: {
                entity.renderMode = 0;
                this.animationAngle = 0f;
                float size = (float)Math.min((data.currentAnimationTime+partialTick)/190d,1d);
                if(data.currentAnimationTime>190) size*=Math.max(((data.currentAnimationTime-190f)/5f),1f);
                final float finalScale = size/2f;
                final float finalAlpha = size;
                double rotation = Math.min((data.currentAnimationTime+partialTick)/200d,1d)*10d;
                data.applyAltRenderSettings(altRender -> altRender.setScale(finalScale,finalScale,finalScale)
                        .setColor(0f,0f,0f,finalAlpha,1f,1f,1f,finalAlpha).setRotations(rotation,rotation,0d));
                return;
            }
            case DAMAGE: {
                float rand = entity.world.rand.nextFloat();
                entity.renderMode = rand<0.8f ? 2 : 1;
                float scaledTime = ((float)data.currentAnimationTime+partialTick)/((float)data.currentAnimation.getTotalTime());
                if(rand<0.2f) scaledTime*=2f;
                this.animationAngle = scaledTime*-25f;
                this.animationAngleScales = entity.getPositionVector().crossProduct(entity.getLook(partialTick)).normalize();
                final double scaledOffset = scaledTime*2d;
                setLimbAnimations(anim -> anim.setRotationDegrees(false,0d,10d,0d).setOffset(scaledOffset),
                        anim -> anim.setOffset(scaledOffset));
                return;
            }
            case TELEPORT: {
                entity.renderMode = 0;
                this.animationAngle = 0f;
                float size = data.currentAnimationTime<4 ? (4f-(float)data.currentAnimationTime-partialTick)/4f :
                        (data.currentAnimationTime+2>=data.currentAnimation.getTotalTime() ?
                                (2f-((data.currentAnimation.getTotalTime()-data.currentAnimationTime-partialTick)))/4f : 0.1f);
                data.applyAltRenderSettings(altRender -> altRender.setScale(size,size,size)
                        .setRotations(0.5d,0.5d,0.5d).setColor(0f,0f,0f,1f/size,1f,1f,1f,1f/size));
                return;
            }
            case DEATH: {
                float rand = entity.world.rand.nextFloat();
                entity.renderMode = rand<0.8f ? 2 : 1;
                float scaledTime = ((float)data.currentAnimationTime+partialTick)/((float)data.currentAnimation.getTotalTime());
                if(rand<0.2f) scaledTime*=2f;
                if(rand<0.1f) scaledTime*=1.5f;
                this.animationAngle = scaledTime*-75f;
                this.animationAngleScales = entity.getPositionVector().crossProduct(entity.getLook(partialTick)).normalize();
                final double scaledOffset = scaledTime*2d;
                setLimbAnimations(anim -> anim.setRotationDegrees(false,30d,0d,30d).setOffset(scaledOffset),
                        anim -> anim.setRotationDegrees(false,15d,0d,15d).setOffset(scaledOffset));
                return;
            }
            case IDLE: {
                entity.renderMode = 1;
                this.animationAngle = 0f;
                resetPartAnimations();
            }
        }
    }

    @Override
    protected void renderModel(NightTerrorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                               float netHeadYaw, float headPitch, float scaleFactor) {
        if(this.animationAngle!=0f) {
            GlStateManager.rotate(this.animationAngle,(float)this.animationAngleScales.x,0f,0f);
            GlStateManager.rotate(this.animationAngle,0f,(float)this.animationAngleScales.y,0f);
            GlStateManager.rotate(this.animationAngle,0f,0f,(float)this.animationAngleScales.z);
        }
        super.renderModel(entity,limbSwing,limbSwingAmount,ageInTicks,netHeadYaw,headPitch,scaleFactor);
    }

    @Override
    protected void applyRotations(NightTerrorEntity entity, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(entity,ageInTicks,rotationYaw,partialTick);
    }

    @Override
    protected boolean setBrightness(NightTerrorEntity entity, float partialTick, boolean combineTextures) {
        return false;
    }

    /**
     * Adjusted implementation of RenderPlayer#setModelVisibilities
     */
    private void setModelVisibilities(NightTerrorEntity entity) {
        ModelPlayer modelplayer = ((ModelPlayer)((ModelBaseCapture)this.getMainModel()).getParentModel());
        modelplayer.setVisible(true);
        modelplayer.bipedHeadwear.showModel = false;
        modelplayer.bipedBodyWear.showModel = false;
        modelplayer.bipedLeftLegwear.showModel = false;
        modelplayer.bipedRightLegwear.showModel = false;
        modelplayer.bipedLeftArmwear.showModel = false;
        modelplayer.bipedRightArmwear.showModel = false;
        modelplayer.bipedCape.showModel = false;
        modelplayer.bipedDeadmau5Head.showModel = false;
        ItemStack stack = entity.getHeldItemMainhand();
        ItemStack stack1 = entity.getHeldItemOffhand();
        modelplayer.isSneak = entity.isSneaking();
        ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
        ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;
        if (!stack.isEmpty()) {
            modelbiped$armpose = ModelBiped.ArmPose.ITEM;
            if (entity.getItemInUseCount() > 0) {
                EnumAction enumaction = stack.getItemUseAction();
                if (enumaction == EnumAction.BLOCK) modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                else if (enumaction == EnumAction.BOW) modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
            }
        }
        if (!stack1.isEmpty()) {
            modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;
            if (entity.getItemInUseCount() > 0) {
                EnumAction enumaction1 = stack1.getItemUseAction();
                if (enumaction1 == EnumAction.BLOCK) modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                else if (enumaction1 == EnumAction.BOW) modelbiped$armpose1 = ModelBiped.ArmPose.BOW_AND_ARROW;
            }
        }
        if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
            modelplayer.rightArmPose = modelbiped$armpose;
            modelplayer.leftArmPose = modelbiped$armpose1;
        }
        else {
            modelplayer.rightArmPose = modelbiped$armpose1;
            modelplayer.leftArmPose = modelbiped$armpose;
        }
    }

    @Override
    protected boolean bindEntityTexture(NightTerrorEntity entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(Objects.isNull(texture)) return false;
        else {
            if(entity.renderMode==2) this.bindTexture(texture);
            return true;
        }
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(NightTerrorEntity entity) {
        return SKIN_TEXTURE;
    }

    @Override
    protected boolean canRenderName(NightTerrorEntity entity) {
        return false;
    }
}
