package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.client.render.geometry.Cube;
import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import net.minecraft.client.renderer.GlStateManager;
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
    public void doRender(@Nonnull NightTerrorEntity entity, double x, double y, double z, float entityYaw, float partialTick) {
        Cube testCube = entity.getTestCube();
        if(Objects.nonNull(testCube) && (x!=0 || y!=0 || z!=0)) {
            preRender();
            testCube.render(x,y+2,z);
            postRender();
        }
    }

    private void preRender() {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
    }

    private void postRender() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(@Nonnull NightTerrorEntity entity) {
        return null;
    }
}
