package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class Convex3D {
    private final TriangleMapper[] triangles;
    private final float[] color = new float[]{1f,1f,1f,1f};
    private final double[] rotationSpeed = new double[]{0d,0d,0d,0d};
    private final float[] currentRotation = new float[]{0f,0f,0f};
    private int displayList;
    private boolean isCompiled = false;

    public Convex3D(Vec3d ... relativeCoords) {
        if(Objects.isNull(relativeCoords) || relativeCoords.length<=3)
            throw new RuntimeException("Only convex polygons with more than 3 vertices are supported fo Convex3D objects");
        this.triangles = new TriangleMapper[relativeCoords.length];
        for(int i=0; i<relativeCoords.length; i++)
            this.triangles[i] = new TriangleMapper(relativeCoords[i],relativeCoords);
    }

    public void setColor(float ... newColor) {
        for(int i=0; i<this.color.length; i++)
            this.color[i] = newColor.length>i ? newColor[i] : 1f;
    }

    public void setRotationSpeed(double ... newSpeed) {
        for(int i=0; i<this.rotationSpeed.length-1; i++)
            this.rotationSpeed[i] = newSpeed.length>i ? newSpeed[i] : 1f;
        double maxSpeed = 0d;
        for(int i=0; i<this.rotationSpeed.length-1; i++) {
            double speed = this.rotationSpeed[i];
            if(speed>maxSpeed) maxSpeed = speed;
        }
        if(maxSpeed>0) this.rotationSpeed[3] = 360d*maxSpeed;
    }

    public void setRandomRotations(Random random, double speedFactor) {
        setRotationSpeed(random.nextDouble()*speedFactor);
    }

    public void render(double x, double y, double z, float partialTick) {
        render(new Vec3d(x,y,z),partialTick);
    }

    public void render(Vec3d pos, float partialTick) {
        GlStateManager.color(this.color[0],this.color[1],this.color[2],this.color[3]);
        GlStateManager.translate(pos.x,pos.y,pos.z);
        applyRotation(partialTick);
        for(TriangleMapper triangle : this.triangles) {
            GlStateManager.glBegin(GL11.GL_TRIANGLE_STRIP);
            for(int i=0; i<triangle.length; i++) {
                vertexFloat(triangle.getOriginal());
                vertexFloat(triangle.getA(i));
                vertexFloat(triangle.getB(i));
            }
            GlStateManager.glEnd();
        }
        renderOutlines();
    }

    private void applyRotation(float partialTick) {
        for(int i = 0; i < this.currentRotation.length; i++)
            this.currentRotation[i] = rotateClampedAxis(i,partialTick);
        GlStateManager.rotate(360f,this.currentRotation[0]/360f,this.currentRotation[1]/360f,this.currentRotation[2]/360f);
    }

    private float rotateClampedAxis(int index, float partialTick) {
        float current = this.currentRotation[index];
        float adjusted = current+(float)(this.rotationSpeed[index]*partialTick);
        while(adjusted>360f) adjusted-=360f;
        return adjusted;
    }

    public void renderOutlines() {
        GlStateManager.color(1f-this.color[0],1f-this.color[1],1f-this.color[2],this.color[3]);
        for(TriangleMapper triangle : this.triangles) {
            for(int i=0; i<triangle.length; i++) {
                GlStateManager.glBegin(GL11.GL_LINES);
                vertexFloat(triangle.getOriginal());
                vertexFloat(triangle.getA(i));
                GlStateManager.glEnd();
                GlStateManager.glBegin(GL11.GL_LINES);
                vertexFloat(triangle.getOriginal());
                vertexFloat(triangle.getB(i));
                GlStateManager.glEnd();
            }
        }
    }


    private void vertexFloat(Vec3d vec) {
        GlStateManager.glVertex3f((float)vec.x,(float)vec.y,(float)vec.z);
    }
}
