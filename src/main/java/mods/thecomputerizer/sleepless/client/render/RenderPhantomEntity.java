package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.mixin.vanilla.InvokerRender;
import mods.thecomputerizer.sleepless.registry.entities.PhantomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class RenderPhantomEntity extends RenderLiving<PhantomEntity> {

    private Render<?> currentRender;
    private EntityLivingBase referenceEntity;

    @SuppressWarnings("DataFlowIssue")
    public RenderPhantomEntity(RenderManager manager) {
        super(manager,null,0.5f);
    }

    public void updateShadowSize() {
        if(Objects.nonNull(this.currentRender))
            this.shadowSize = this.currentRender.shadowSize;
    }

    private void makeReferenceEntity(@Nonnull PhantomEntity entity) {
        Render<?> nextRender = entity.getShadowRender(this.renderManager);
        if(Objects.isNull(this.currentRender) || (Objects.nonNull(nextRender) && this.currentRender!=nextRender)) {
            this.currentRender = nextRender;
            if(this.currentRender instanceof RenderPlayer) {
                this.mainModel = ((RenderPlayer)this.currentRender).getMainModel();
                this.referenceEntity = Minecraft.getMinecraft().player;
            }
            else if(this.currentRender instanceof RenderLivingBase<?>) {
                this.mainModel = ((RenderLivingBase<?>)this.currentRender).getMainModel();
                try {
                    this.referenceEntity = (EntityLivingBase)entity.getShadowEntityClass()
                            .getConstructor(World.class).newInstance(entity.world);
                } catch (Exception e) {
                    Constants.LOGGER.error("FAILED TO INSTANTIATE REFERENCE ENTITY! ", e);
                }
            }
            updateShadowSize();
        }
    }

    @Override
    public void doRender(@Nonnull PhantomEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entity, this, partialTicks, x, y, z))) return;
        makeReferenceEntity(entity);
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        if(Objects.nonNull(this.referenceEntity) && canYouSeeMe()) {
            this.mainModel.swingProgress = entity.getSwingProgress(partialTicks);
            boolean shouldSit = entity.isRiding() && (Objects.nonNull(entity.getRidingEntity()) && entity.getRidingEntity().shouldRiderSit());
            this.mainModel.isRiding = shouldSit;
            this.mainModel.isChild = entity.isChild();
            try {
                float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
                float f2 = f1 - f;
                if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) entity.getRidingEntity();
                    f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                    f2 = f1 - f;
                    float f3 = MathHelper.wrapDegrees(f2);
                    if (f3 < -85.0F) f3 = -85.0F;
                    if (f3 >= 85.0F) f3 = 85.0F;
                    f = f1 - f3;
                    if (f3 * f3 > 2500.0F) f += f3 * 0.2F;
                    f2 = f1 - f;
                }
                float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                this.renderLivingAt(entity, x, y, z);
                float f8 = this.handleRotationFloat(entity, partialTicks);
                this.applyRotations(entity, f8, f, partialTicks);
                float f4 = this.prepareScale(entity, partialTicks);
                float f5 = 0.0F;
                float f6 = 0.0F;
                if (!entity.isRiding()) {
                    f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                    f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
                    if (entity.isChild()) f6 *= 3.0F;
                    if (f5 > 1.0F) f5 = 1.0F;
                    f2 = f1 - f;
                }
                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations(this.referenceEntity, f6, f5, partialTicks);
                this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, this.referenceEntity);
                if (this.renderOutlines) {
                    boolean flag1 = this.setScoreTeamColor(entity);
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                    if (!this.renderMarker) renderShadowModel(entity, f6, f5, f8, f2, f7, f4);
                    renderShadowLayers(entity,f6,f5,partialTicks,f8,f2,f7,f4);
                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();
                    if (flag1) this.unsetScoreTeamColor();
                } else {
                    boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                    renderShadowModel(entity, f6, f5, f8, f2, f7, f4);
                    if (flag) this.unsetBrightness();
                    GlStateManager.depthMask(true);
                    renderShadowLayers(entity,f6,f5,partialTicks,f8,f2,f7,f4);
                }
                GlStateManager.disableRescaleNormal();
            } catch (Exception e) {
                Constants.LOGGER.error("Couldn't render phantom entity", e);
            }
        }
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        if(Objects.nonNull(this.referenceEntity) && canYouSeeMe()) {
            if (!this.renderOutlines) this.renderName(entity, x, y, z);
            MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entity, this, partialTicks, x, y, z));
        }
    }

    protected void renderShadowModel(@Nonnull PhantomEntity entity, float swing, float swingAmount, float ageInTicks,
                                     float headYaw, float headPitch, float scale) {
        boolean flag = this.isVisible(entity);
        boolean flag1 = !flag && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
        if (flag || flag1) {
            if (!this.bindEntityTexture(entity)) return;
            applyColor();
            this.mainModel.render(this.referenceEntity,swing,swingAmount,ageInTicks,headYaw,headPitch,scale);
            finishColor();
        }
    }

    @SuppressWarnings("unchecked")
    protected void renderShadowLayers(@Nonnull PhantomEntity entity, float swing, float swingAmount, float partialTick,
                                      float ageInTicks, float headYaw, float headPitch, float scale) {
        if(this.currentRender instanceof RenderLivingBase<?>) {
            for (LayerRenderer<?> layer : ((RenderLivingBase<?>)this.currentRender).layerRenderers) {
                LayerRenderer<EntityLivingBase> baseLayer = (LayerRenderer<EntityLivingBase>)layer;
                boolean flag = this.setBrightness(entity,partialTick,baseLayer.shouldCombineTextures());
                baseLayer.doRenderLayer(this.referenceEntity,swing,swingAmount,partialTick,ageInTicks,headYaw,headPitch,scale);
                if (flag) this.unsetBrightness();
            }
        }
    }

    private void applyColor() {
        float reversePhantom = 1f-ClientEffects.PHANTOM_VISIBILITY;
        GlStateManager.color(reversePhantom,reversePhantom,reversePhantom,ClientEffects.PHANTOM_VISIBILITY);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516,0.003921569f);
    }

    private void finishColor() {
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516,0.1f);
        GlStateManager.depthMask(true);
    }

    @Override
    public void doRenderShadowAndFire(@Nonnull Entity entity, double x, double y, double z, float yaw, float partialTick) {
        if(canYouSeeMe()) super.doRenderShadowAndFire(entity,x,y,z,yaw,partialTick);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @Nullable ResourceLocation getEntityTexture(@Nonnull PhantomEntity entity) {
        return Objects.nonNull(this.currentRender) ? Objects.nonNull(this.referenceEntity) ?
                (((InvokerRender<EntityLivingBase>)this.currentRender).callGetEntityTexture(this.referenceEntity)) : null : null;
    }

    private boolean canYouSeeMe() {
        return ClientEffects.PHANTOM_VISIBILITY>0;
    }
}
