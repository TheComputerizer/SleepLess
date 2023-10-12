package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class StaticGeometryRender {

    public static final List<StaticGeometryRender> STATIC_RENDERS = Collections.synchronizedList(new ArrayList<>());

    private final RenderManager manager;
    private final List<ShapeHolder> freeShapeRenders;
    private final List<Column> columnRenders;
    private final Vec3d renderVec;

    public StaticGeometryRender(RenderManager manager, Vec3d renderVec) {
        this.manager = manager;
        this.freeShapeRenders = new ArrayList<>();
        this.columnRenders = new ArrayList<>();
        this.renderVec = renderVec;
    }

    public void addColumn(Column column) {
        this.columnRenders.add(column);
    }

    public void addFreeShape(ShapeHolder holder) {
        this.freeShapeRenders.add(holder);
    }

    public void render(float partialTick) {
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if(Objects.nonNull(entity) && Objects.nonNull(this.renderVec)) {
            double renderX = entity.lastTickPosX+(entity.posX-entity.lastTickPosX)*(double)partialTick;
            double renderY = entity.lastTickPosY+(entity.posY-entity.lastTickPosY)*(double)partialTick;
            double renderZ = entity.lastTickPosZ+(entity.posZ-entity.lastTickPosZ)*(double)partialTick;
            Vec3d renderAt = this.renderVec.subtract(renderX,renderY,renderZ);
            for(Column column : this.columnRenders) column.render(renderAt);
            for(ShapeHolder holder : this.freeShapeRenders) holder.render(renderAt);
        }
    }
}
