package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.client.model.ModelBox;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Used for standalone renders like the column
 */
public class ShapeHolder {

    public static ShapeHolder fromModelBox(ModelBox boxModel, double inherentScaling, Consumer<Convex3D> shapeSettings,
                                           Consumer<ShapeHolder> holderSettings) {
        Vec3d radii = new Vec3d(Math.abs(boxModel.posX2-boxModel.posX1)/2d,
                Math.abs(boxModel.posY2-boxModel.posY1)/2d,
                Math.abs(boxModel.posZ2-boxModel.posZ1)/2d);
        Vec3d center = new Vec3d(boxModel.posX1+radii.x,boxModel.posY1+radii.y,boxModel.posZ1+radii.z);
        Vec3d normalizedRadii = radii.normalize().scale(inherentScaling);
        Convex3D box = Shapes.BOX.makeInstance();
        box.setScale((float)normalizedRadii.x,(float)normalizedRadii.y,(float)normalizedRadii.z);
        shapeSettings.accept(box);
        ShapeHolder holder = new ShapeHolder(box);
        holder.setRelativePosition(center.scale(inherentScaling));
        holderSettings.accept(holder);
        return holder;
    }

    private final Convex3D shape;
    private final List<ShapeHolder> childHolders;
    private boolean isMoving;
    private Vec3d relativePosVec;
    private Vec3d dirVec;

    public ShapeHolder(Convex3D shape) {
        this.shape = shape;
        this.childHolders = new ArrayList<>();
        this.relativePosVec = Vec3d.ZERO;
    }

    public void addChild(Convex3D shape, Consumer<ShapeHolder> holderSettings) {
        ShapeHolder childHolder = new ShapeHolder(shape);
        holderSettings.accept(childHolder);
        addChild(childHolder);
    }

    public void addChild(ShapeHolder childHolder) {
        this.childHolders.add(childHolder);
    }

    public void startMoving() {
        this.isMoving = true;
    }

    public ShapeHolder setRelativePosition(Vec3d relativePos) {
        this.relativePosVec = relativePos;
        return this;
    }

    public void setRelativeBottom() {
        setRelativePosition(new Vec3d(0d,this.shape.getScaledHeight(),0d));
    }

    public ShapeHolder setDirection(Vec3d dirVec) {
        this.dirVec = dirVec;
        return this;
    }

    public ShapeHolder setRotations(double x, double y, double z) {
        this.shape.setRotationSpeed(x,y,z);
        return this;
    }

    public ShapeHolder setScale(float x, float y, float z) {
        this.shape.setScale(x,y,z);
        return this;
    }

    public ShapeHolder setColor(float ... colors) {
        this.shape.setColor(colors);
        return this;
    }

    public void stopMoving() {
        this.isMoving = false;
    }

    public Vec3d getRelativePosition() {
        return this.relativePosVec;
    }

    public void render(Vec3d relativeCenter) {
        this.shape.render(relativeCenter.add(this.relativePosVec));
        if(this.isMoving) setRelativePosition(this.relativePosVec.add(this.dirVec));
        for(ShapeHolder child : this.childHolders) child.render(relativeCenter);
    }

    public void renderScaledRelative(Vec3d relativeCenter, float scale) {
        this.shape.render(relativeCenter.add(this.relativePosVec.scale(scale)));
        if(this.isMoving) setRelativePosition(this.relativePosVec.add(this.dirVec));
        for(ShapeHolder child : this.childHolders) child.render(relativeCenter);
    }
}
