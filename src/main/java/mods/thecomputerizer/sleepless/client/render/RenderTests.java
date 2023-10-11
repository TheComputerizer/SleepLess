package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderTests {

    private static final List<ModelRotatingBox> ROTATING_BOX_RENDERS = new ArrayList<>();

    public static void renderRotatingBox(Vec3d renderCenter, double xRot, double yRot, double zRot, int ticks) {
        ModelRotatingBox box = new ModelRotatingBox(Minecraft.getMinecraft().world.rand,5f,ticks);
        box.setCenter(renderCenter).setDimensions(new Vec3d(16d,16d,16d))
                .overrideRotations(new Vec3d(xRot,yRot,zRot));
        if(box.init()) ROTATING_BOX_RENDERS.add(box);
        else Constants.LOGGER.error("Rotating box failed render test!");
    }

    public static void onClientTick() {
        ROTATING_BOX_RENDERS.removeIf(box -> !box.onClientTick());
    }

    public static void onRenderWorld(@Nonnull RenderManager manager, float partialTick) {
        preRender();
        for(ModelRotatingBox box : ROTATING_BOX_RENDERS)
            box.render(manager,Tessellator.getInstance().getBuffer(),partialTick);
        postRender();
    }

    private static void preRender() {
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

    private static void postRender() {
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
}