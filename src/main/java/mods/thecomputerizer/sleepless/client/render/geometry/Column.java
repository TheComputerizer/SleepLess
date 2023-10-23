package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
public class Column {

    private final Random random;
    private final Vec3d relativeBottom;
    private final double height;
    private final double radius;
    private final double spacing;
    private final ShapeHolder outline;
    private final List<ShapeHolder> movingShapes;
    private double shapeSpeed;
    private ShapeHolder recentShape;

    public Column(Random random, Vec3d relativeBottom, double height, double radius, double spacing) {
        this.random = random;
        this.relativeBottom = relativeBottom;
        this.height = height;
        this.radius = radius;
        this.spacing = spacing;
        this.outline = makeOutlineShape();
        this.movingShapes = new ArrayList<>();
        this.shapeSpeed = 1d;
    }

    private ShapeHolder makeOutlineShape() {
        Convex3D column = Shapes.BOX.makeInstance();
        column.setColor(0.4f,0.6f,0.9f,0.1f);
        column.setScale((float)this.radius,(float)this.height,(float)this.radius);
        column.setRotationSpeed(0d,0.075d,0d);
        column.setEnableOutline(false);
        return new ShapeHolder(column).setRelativePosition(this.relativeBottom.add(0d,this.height/2d,0d));
    }

    public void setSpeed(double speed) {
        this.shapeSpeed = speed;
        for(ShapeHolder holder : this.movingShapes)
            holder.setDirection(new Vec3d(0d,0.04d*this.shapeSpeed,0d));
    }

    public void render(Vec3d relativeCenter) {
        Vec3d actualRender = relativeCenter.add(this.relativeBottom.x,0d,this.relativeBottom.z);
        this.outline.render(actualRender);
        if(this.movingShapes.isEmpty() || Objects.isNull(this.recentShape) ||
                this.recentShape.getRelativePosition().y-this.relativeBottom.y>this.spacing) {
            ShapeHolder newholder = new ShapeHolder(generateRandomBox())
                    .setRelativePosition(this.relativeBottom).setDirection(new Vec3d(0d,0.04d*this.shapeSpeed,0d));
            newholder.startMoving();
            this.movingShapes.add(newholder);
            this.recentShape = newholder;
        }
        Iterator<ShapeHolder> shapesIterator = this.movingShapes.listIterator();
        while(shapesIterator.hasNext()) {
            ShapeHolder holder = shapesIterator.next();
            holder.render(actualRender);
            if(holder.getRelativePosition().y+this.spacing>this.relativeBottom.y+height)
                shapesIterator.remove();
        }
    }

    private Convex3D generateRandomBox() {
        Convex3D newShape = Shapes.BOX.makeInstance();
        newShape.setColor(0f,0f,0f,0.5f);
        float defaultScale = 0.5f*Math.min((float)this.radius,(float)this.spacing);
        newShape.setScale(defaultScale+(float)randomOffset(defaultScale/2f),
                defaultScale+(float)randomOffset(defaultScale),
                defaultScale+(float)randomOffset(defaultScale/2f));
        newShape.setRotationSpeed(0.3d+randomOffset(0.2d),0.1d+randomOffset(0.2d),0.2d+randomOffset(0.2d));
        return newShape;
    }

    private double randomOffset(double range) {
        return (-range/2d)+(this.random.nextDouble()*range);
    }
}
