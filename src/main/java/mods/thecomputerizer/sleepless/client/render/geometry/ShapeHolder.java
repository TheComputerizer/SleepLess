package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.util.math.Vec3d;

/**
 * Used for standalone renders like the column
 */
public class ShapeHolder {

    private final Convex3D shape;
    private boolean isMoving;
    private Vec3d relativePosVec;
    private Vec3d dirVec;

    public ShapeHolder(Convex3D shape) {
        this.shape = shape;
    }

    public void startMoving() {
        this.isMoving = true;
    }

    public ShapeHolder setRelativePosition(Vec3d relativePos) {
        this.relativePosVec = relativePos;
        return this;
    }

    public ShapeHolder setDirection(Vec3d dirVec) {
        this.dirVec = dirVec;
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
    }
}
