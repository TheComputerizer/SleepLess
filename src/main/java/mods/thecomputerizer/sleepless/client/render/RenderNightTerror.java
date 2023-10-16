package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.client.render.geometry.Convex3D;
import mods.thecomputerizer.sleepless.client.render.geometry.ModelBaseCapture;
import mods.thecomputerizer.sleepless.client.render.geometry.ShapeHolder;
import mods.thecomputerizer.sleepless.client.render.geometry.Shapes;
import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class RenderNightTerror extends RenderLivingBase<NightTerrorEntity> {

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
    public void doRender(@Nonnull NightTerrorEntity entity, double x, double y, double z, float entityYaw, float partialTick) {
        if(entity.ticksExisted<200) renderSpawning(entity.ticksExisted,x,y,z,partialTick);
        else {
            double sneakOffset = entity.isSneaking() ? y - 0.125d : y;
            this.setModelVisibilities(entity);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            super.doRender(entity, x, sneakOffset, z, entityYaw, partialTick);
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }

    private void renderSpawning(double ticks, double x, double y, double z, double partialTick) {
        if(Objects.isNull(this.spawnRender)) this.spawnRender = new ShapeHolder(Shapes.BOX.makeInstance());
        float size = (float)Math.min((ticks+partialTick)/100d,1d);
        this.spawnRender.setScale(size/4f,size/4f,size/4f).setColor(0f,0f,0f,size,1f,1f,1f,size);
        double rotation = Math.min((ticks+partialTick)/200d,1d)*10d;
        this.spawnRender.setRotations(rotation,rotation,0d).setRelativePosition(Vec3d.ZERO);
        this.spawnRender.render(new Vec3d(x,y+2,z));

    }

    /**
     * Adjusted implementation of RenderPlayer#setModelVisibilities
     */
    private void setModelVisibilities(@Nonnull NightTerrorEntity entity) {
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
    protected boolean bindEntityTexture(@Nonnull NightTerrorEntity entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(Objects.isNull(texture)) return false;
        else {
            if(this.isTextured) this.bindTexture(texture);
            return true;
        }
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(@Nonnull NightTerrorEntity entity) {
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    public void setTextured(boolean isTextured) {
        this.isTextured = isTextured;
    }

    @Override
    protected boolean canRenderName(@Nonnull NightTerrorEntity entity) {
        return false;
    }
}
