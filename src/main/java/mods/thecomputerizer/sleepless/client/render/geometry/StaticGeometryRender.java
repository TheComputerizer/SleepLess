package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
public class StaticGeometryRender {

    public static final List<StaticGeometryRender> STATIC_RENDERS = Collections.synchronizedList(new ArrayList<>());

    private final RenderManager manager;
    private final List<ShapeHolder> freeShapeRenders;
    private final List<Column> columnRenders;
    private Vec3d renderVec;

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

    public void setRenderVec(Vec3d renderVec) {
        this.renderVec = renderVec;
    }

    public void setRenderXZ(double x, double z) {
        this.renderVec = new Vec3d(x,this.renderVec.y,z);
    }

    public void render(float partialTick) {
        if(isEmpty()) return;
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if(Objects.nonNull(entity) && Objects.nonNull(this.renderVec)) {
            double renderX = entity.lastTickPosX+(entity.posX-entity.lastTickPosX)*(double)partialTick;
            double renderY = entity.lastTickPosY+(entity.posY-entity.lastTickPosY)*(double)partialTick;
            double renderZ = entity.lastTickPosZ+(entity.posZ-entity.lastTickPosZ)*(double)partialTick;
            Vec3d renderAt = this.renderVec.subtract(renderX,renderY,renderZ);
            Iterator<Column> columnItr = this.columnRenders.iterator();
            while(columnItr.hasNext()) {
                Column column = columnItr.next();
                if(column instanceof ITickableGeometry && !((ITickableGeometry<?>)column).isInitialized())
                    columnItr.remove();
                else column.render(renderAt);
            }
            Iterator<ShapeHolder> holderItr = this.freeShapeRenders.iterator();
            while(holderItr.hasNext()) {
                ShapeHolder holder = holderItr.next();
                if(holder instanceof ITickableGeometry && !((ITickableGeometry<?>)holder).isInitialized())
                    holderItr.remove();
                else holder.render(renderAt);
            }
        }
    }

    public boolean isEmpty() {
        return this.columnRenders.isEmpty() && this.freeShapeRenders.isEmpty();
    }
}
