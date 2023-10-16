package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.client.render.geometry.ModelBaseCapture;
import mods.thecomputerizer.sleepless.client.render.geometry.ShapeHolder;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
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

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class RenderNightTerror extends RenderLivingBase<NightTerrorEntity> {

    private static final ResourceLocation SKIN_TEXTURE = Constants.res("textures/entity/night_terror.png");

    private ShapeHolder spawnRender;
    private boolean isTextured = false;

    public RenderNightTerror(RenderManager manager) {
        super(manager,new ModelBaseCapture(new ModelPlayer(0f,true)),0f);
        getPlayerModel().isChild = false;
    }

    private ModelPlayer getPlayerModel() {
        return ((ModelPlayer)((ModelBaseCapture)this.getMainModel()).getParentModel());
    }

    @Override
    public void doRender(NightTerrorEntity entity, double x, double y, double z, float entityYaw, float partialTick) {
        applyCustomAnimations(entity,partialTick);
        if(entity.renderMode==0) entity.getAnimationData().renderAlt(new Vec3d(x,y+1.5d,z));
        else {
            ((ModelBaseCapture)getMainModel()).setVisibility(entity.renderMode==1);
            double sneakOffset = entity.isSneaking() ? y-0.125d : y;
            this.setModelVisibilities(entity);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            super.doRender(entity,x,sneakOffset,z,entityYaw, partialTick);
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }

    private void applyCustomAnimations(NightTerrorEntity entity, float partialTick) {
        NightTerrorEntity.AnimationData data = entity.getAnimationData();
        switch(data.currentAnimation) {
            case SPAWN: {
                entity.renderMode = 0;
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
                entity.renderMode = entity.world.rand.nextFloat()<0.8f ? 2 : 1;
                return;
            }
            case TELEPORT: {
                entity.renderMode = 0;
                float size = Math.abs((data.currentAnimationTime%10)-5.5f)+partialTick;
                data.applyAltRenderSettings(altRender -> altRender.setScale(0.5f/size,0.5f/size,0.5f/size)
                        .setRotations(0.5d,0.5d,0.5d).setColor(0f,0f,0f,size/5.5f,1f,1f,1f,size/5.5f));
                return;
            }
            case DEATH: {
                entity.renderMode = 2;
                return;
            }
            default: entity.renderMode = 1;
        }
    }

    @Override
    protected void applyRotations(NightTerrorEntity entity, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(entity,ageInTicks,rotationYaw,partialTick);
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
