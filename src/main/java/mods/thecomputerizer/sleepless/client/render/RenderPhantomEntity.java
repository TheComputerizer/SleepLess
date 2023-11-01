package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.mixin.vanilla.InvokerRender;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

    @SuppressWarnings("DataFlowIssue")
    public RenderPhantomEntity(RenderManager manager) {
        super(manager,null,0.5f);
    }

    public void updateSize(@Nonnull PhantomEntity entity) {
        if(Objects.nonNull(entity.currentRender)) {
            this.shadowSize = entity.currentRender.shadowSize;
            if(Objects.nonNull(entity.referenceEntity))
                entity.updateSize(entity.referenceEntity.width,entity.referenceEntity.height);
        }
    }

    private void makeReferenceEntity(@Nonnull PhantomEntity entity) {
        Render<?> nextRender = entity.getShadowRender(this.renderManager);
        if(Objects.isNull(entity.currentRender) || (Objects.nonNull(nextRender) && entity.currentRender!=nextRender)) {
            entity.currentRender = nextRender;
            if(entity.currentRender instanceof RenderPlayer)
                entity.referenceEntity = Minecraft.getMinecraft().player;
            else if(entity.currentRender instanceof RenderLivingBase<?>) {
                try {
                    entity.referenceEntity = (EntityLivingBase)entity.getShadowEntityClass()
                            .getConstructor(World.class).newInstance(entity.world);
                } catch (Exception e) {
                    Constants.LOGGER.error("FAILED TO INSTANTIATE REFERENCE ENTITY! ", e);
                }
            }
            updateSize(entity);
        }
    }

    @SuppressWarnings("ConstantValue")
    private @Nullable ModelBase getModel(@Nonnull Render<?> render) {
        return render instanceof RenderLivingBase<?> ? ((RenderLivingBase<?>)render).getMainModel() :
                (render instanceof RenderPlayer ? ((RenderPlayer)render).getMainModel() : null);
    }

    private void setModel(@Nonnull PhantomEntity entity) {
        this.mainModel = Objects.nonNull(entity.referenceEntity) && Objects.nonNull(entity.currentRender) ?
                getModel(entity.currentRender) : null;
    }

    @Override
    public void doRender(@Nonnull PhantomEntity entity, double x, double y, double z, float entityYaw, float partialTick) {
        if(MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entity,this,partialTick,x,y,z))) return;
        makeReferenceEntity(entity);
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        setModel(entity);
        if(Objects.nonNull(this.mainModel) && canYouSeeMe()) {
            this.mainModel.swingProgress = entity.getSwingProgress(partialTick);
            boolean shouldSit = entity.isRiding() && (Objects.nonNull(entity.getRidingEntity()) && entity.getRidingEntity().shouldRiderSit());
            this.mainModel.isRiding = shouldSit;
            this.mainModel.isChild = entity.isChild();
            try {
                float yawOffset = this.interpolateRotation(entity.prevRenderYawOffset,entity.renderYawOffset,partialTick);
                float rotationYaw = this.interpolateRotation(entity.prevRotationYawHead,entity.rotationYawHead,partialTick);
                float adjustedYaw = rotationYaw-yawOffset;
                if(shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
                    EntityLivingBase base = (EntityLivingBase)entity.getRidingEntity();
                    yawOffset = this.interpolateRotation(base.prevRenderYawOffset,base.renderYawOffset,partialTick);
                    adjustedYaw = rotationYaw-yawOffset;
                    float wrappedYaw = MathHelper.wrapDegrees(adjustedYaw);
                    if(wrappedYaw<-85.0f) wrappedYaw = -85.0f;
                    if(wrappedYaw>=85.0f) wrappedYaw = 85.0f;
                    yawOffset = rotationYaw-wrappedYaw;
                    if(wrappedYaw *wrappedYaw>2500f) yawOffset+=wrappedYaw*0.2f;
                    adjustedYaw = rotationYaw-yawOffset;
                }
                float rotationPitch = entity.prevRotationPitch+(entity.rotationPitch-entity.prevRotationPitch)*partialTick;
                this.renderLivingAt(entity,x,y,z);
                float rotationFloat = this.handleRotationFloat(entity,partialTick);
                this.applyRotations(entity,rotationFloat,yawOffset,partialTick);
                float scale = this.prepareScale(entity,partialTick);
                float lingSwingAmountChange = 0.0F;
                float limbSwingChange = 0.0F;
                if(!entity.isRiding()) {
                    lingSwingAmountChange = entity.prevLimbSwingAmount+(entity.limbSwingAmount-entity.prevLimbSwingAmount)*partialTick;
                    limbSwingChange = entity.limbSwing-entity.limbSwingAmount*(1f-partialTick);
                    if(entity.isChild()) limbSwingChange*=3f;
                    if(lingSwingAmountChange>1f) lingSwingAmountChange = 1f;
                    adjustedYaw = rotationYaw-yawOffset;
                }
                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations(entity.referenceEntity,limbSwingChange,lingSwingAmountChange,partialTick);
                this.mainModel.setRotationAngles(limbSwingChange,lingSwingAmountChange,rotationFloat,adjustedYaw,rotationPitch,scale,entity.referenceEntity);
                if(this.renderOutlines) {
                    boolean setTeamColor = this.setScoreTeamColor(entity);
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                    if(!this.renderMarker) renderShadowModel(entity,limbSwingChange,lingSwingAmountChange,rotationFloat,adjustedYaw,rotationPitch,scale);
                    renderShadowLayers(entity,limbSwingChange,lingSwingAmountChange,partialTick,rotationFloat,adjustedYaw,rotationPitch,scale);
                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();
                    if(setTeamColor) this.unsetScoreTeamColor();
                } else {
                    boolean setBrightness = this.setDoRenderBrightness(entity,partialTick);
                    renderShadowModel(entity,limbSwingChange,lingSwingAmountChange,rotationFloat,adjustedYaw,rotationPitch,scale);
                    if(setBrightness) this.unsetBrightness();
                    GlStateManager.depthMask(true);
                    renderShadowLayers(entity,limbSwingChange,lingSwingAmountChange,partialTick,rotationFloat,adjustedYaw,rotationPitch,scale);
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
        if(Objects.nonNull(entity.referenceEntity) && canYouSeeMe()) {
            if(!this.renderOutlines) this.renderName(entity,x,y,z);
            MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entity,this,partialTick,x,y,z));
        }
    }

    protected void renderShadowModel(@Nonnull PhantomEntity entity, float swing, float swingAmount, float ageInTicks,
                                     float headYaw, float headPitch, float scale) {
        boolean isVisible = this.isVisible(entity);
        boolean isVisibleAnyways = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
        if(isVisible || isVisibleAnyways) {
            if(!this.bindEntityTexture(entity)) return;
            applyColor();
            this.mainModel.render(entity.referenceEntity,swing,swingAmount,ageInTicks,headYaw,headPitch,scale);
            finishColor();
        }
    }

    @SuppressWarnings("unchecked")
    protected void renderShadowLayers(@Nonnull PhantomEntity entity, float swing, float swingAmount, float partialTick,
                                      float ageInTicks, float headYaw, float headPitch, float scale) {
        if(!(entity.referenceEntity instanceof EntityPlayer) && entity.currentRender instanceof RenderLivingBase<?>) {
            for(LayerRenderer<?> layer : ((RenderLivingBase<?>)entity.currentRender).layerRenderers) {
                LayerRenderer<EntityLivingBase> baseLayer = (LayerRenderer<EntityLivingBase>)layer;
                boolean flag = this.setBrightness(entity,partialTick,baseLayer.shouldCombineTextures());
                baseLayer.doRenderLayer(entity.referenceEntity,swing,swingAmount,partialTick,ageInTicks,headYaw,headPitch,scale);
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
        return Objects.nonNull(entity.currentRender) ? Objects.nonNull(entity.referenceEntity) ?
                (((InvokerRender<EntityLivingBase>)entity.currentRender).callGetEntityTexture(entity.referenceEntity)) : null : null;
    }

    private boolean canYouSeeMe() {
        return ClientEffects.PHANTOM_VISIBILITY>0;
    }
}
