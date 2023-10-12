package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.client.render.geometry.Column;
import mods.thecomputerizer.sleepless.client.render.geometry.Convex3D;
import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
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
        if(x!=0 || y!=0 || z!=0) {
            Vec3d renderPos = new Vec3d(x,y,z);
            Convex3D testCube = entity.getTestCube();
            if(Objects.nonNull(testCube)) testCube.render(renderPos);
            Column testColumn = entity.getTestColumn();
            if(Objects.nonNull(testColumn)) testColumn.render(renderPos);
        }
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(@Nonnull NightTerrorEntity entity) {
        return null;
    }
}
