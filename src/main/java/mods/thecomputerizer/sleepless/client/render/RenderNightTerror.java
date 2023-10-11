package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class RenderNightTerror extends RenderLiving<NightTerrorEntity> {

    public RenderNightTerror(RenderManager manager) {
        super(manager,null,0.5f);
    }

    @Override
    public void doRender(@Nonnull NightTerrorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelRotatingBox box = entity.getTestBox(x,y+3,z);
        if(Objects.nonNull(box)) {
            preRender();
            box.render(this.renderManager,Tessellator.getInstance().getBuffer(),partialTicks);
            postRender();
        }
    }

    private void preRender() {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1f,1f,1f,1f);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516,0.003921569f);
    }

    private void postRender() {
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516,0.1f);
        GlStateManager.depthMask(true);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(@Nonnull NightTerrorEntity entity) {
        return null;
    }
}
