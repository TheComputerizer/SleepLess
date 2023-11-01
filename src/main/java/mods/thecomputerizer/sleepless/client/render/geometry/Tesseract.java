package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.util.math.Vec3d;

public class Tesseract extends ShapeHolder {

    private final Convex3D innerShape;
    private Vec3d maxScale = new Vec3d(1d,1d,1d);
    private int scaleCounter;
    private boolean counterReversal = false;
    public Tesseract(Convex3D shape) {
        super(shape);
        this.innerShape = new Convex3D(shape);
    }

    @Override
    public ShapeHolder setRotations(double x, double y, double z) {
        this.shape.setRotationSpeed(x,y,z);
        this.innerShape.setRotationSpeed(x,y,z);
        return this;
    }

    @Override
    public ShapeHolder setScale(float x, float y, float z) {
        this.maxScale = new Vec3d(x,y,z);
        this.shape.setScale(x,y,z);
        this.innerShape.setScale(x/2f,y/2f,z/2f);
        return this;
    }

    @Override
    public ShapeHolder setColor(float ... colors) {
        this.shape.setColor(colors);
        this.innerShape.setColor(colors);
        return this;
    }

    @Override
    public void render(Vec3d relativeCenter) {
        if(this.scaleCounter>=200) this.counterReversal = true;
        else if(this.scaleCounter<0) this.counterReversal = false;
        if(this.counterReversal) this.scaleCounter--;
        else this.scaleCounter++;
        //float scale = 1f/(float)this.scaleCounter;
        //this.shape.setScale(getScale((float)this.maxScale.x,scale),getScale((float)this.maxScale.y,scale),getScale((float)this.maxScale.z,scale));
        //scale = 1f-scale;
        //this.innerShape.setScale(getScale((float)this.maxScale.x,scale),getScale((float)this.maxScale.y,scale),getScale((float)this.maxScale.z,scale));
        super.render(relativeCenter);
        this.innerShape.render(relativeCenter.add(this.relativePosVec));
    }

    @Override
    public void renderScaledRelative(Vec3d relativeCenter, float s) {
        if(this.scaleCounter>=200) this.counterReversal = true;
        else if(this.scaleCounter<0) this.counterReversal = false;
        if(this.counterReversal) this.scaleCounter--;
        else this.scaleCounter++;
        //float scale = 1f/(float)this.scaleCounter;
        //this.shape.setScale(getScale((float)this.maxScale.x,scale),getScale((float)this.maxScale.y,scale),getScale((float)this.maxScale.z,scale));
        //scale = 1f-scale;
        //this.shape.setScale(getScale((float)this.maxScale.x,scale),getScale((float)this.maxScale.y,scale),getScale((float)this.maxScale.z,scale));
        super.renderScaledRelative(relativeCenter,s);
        this.innerShape.render(relativeCenter.add(this.relativePosVec.scale(s)));
    }

    private float getScale(float scale, float factor) {
        return scale-((scale/2f)*factor);
    }
}
