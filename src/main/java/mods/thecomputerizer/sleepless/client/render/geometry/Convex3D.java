package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Random;

public class Convex3D {

    private final TriangleMapper[] triangles;
    private final float[] color = new float[]{1f,1f,1f,1f};
    private final float[] scale = new float[]{1f,1f,1f};
    private final double[] rotationSpeed = new double[]{0d,0d,0d};
    private final float[] currentRotation = new float[]{0f,0f,0f};

    public Convex3D(Vec3d ... relativeCoords) {
        if(Objects.isNull(relativeCoords) || relativeCoords.length<=3)
            throw new RuntimeException("Only convex polygons with more than 3 vertices are supported for Convex3D objects");
        this.triangles = new TriangleMapper[relativeCoords.length];
        for(int i=0; i<relativeCoords.length; i++)
            this.triangles[i] = new TriangleMapper(relativeCoords[i],relativeCoords);
    }

    public void setColor(float ... newColor) {
        for(int i=0; i<this.color.length; i++)
            this.color[i] = newColor.length>i ? newColor[i] : 1f;
    }

    public void setScale(float ... newScale) {
        for(int i=0; i<this.scale.length; i++)
            this.scale[i] = newScale.length>i ? newScale[i] : 1f;
    }

    public void setRotationSpeed(double ... newSpeed) {
        for(int i=0; i<this.rotationSpeed.length; i++)
            this.rotationSpeed[i] = newSpeed.length>i ? newSpeed[i] : 1d;
    }

    public void setRandomRotations(Random random, double speedFactor) {
        setRotationSpeed(random.nextDouble()*speedFactor,random.nextDouble()*speedFactor,random.nextDouble()*speedFactor);
    }

    public void render(double x, double y, double z) {
        render(new Vec3d(x,y,z));
    }

    public void render(Vec3d pos) {
        GlStateManager.color(this.color[0],this.color[1],this.color[2],this.color[3]);
        GlStateManager.scale(this.scale[0],this.scale[1],this.scale[2]);
        GlStateManager.translate(pos.x/this.scale[0],pos.y/this.scale[1],pos.z/this.scale[2]);
        for(int i = 0; i < this.currentRotation.length; i++)
            this.currentRotation[i] = rotateClampedAxis(i);
        GlStateManager.rotate(this.currentRotation[0],1f,0f,0f);
        GlStateManager.rotate(this.currentRotation[1],0f,1f,0f);
        GlStateManager.rotate(this.currentRotation[2],0f,0f,1f);
        for(TriangleMapper triangle : this.triangles)
            renderTriangle(triangle);
        renderOutlines();
    }

    private float rotateClampedAxis(int index) {
        float current = this.currentRotation[index];
        double adjustedSpeed = this.rotationSpeed[index];
        current = current+(float)adjustedSpeed;
        while(current>360f) current-=360f;
        return Math.max(current,0f);
    }

    public void renderTriangle(TriangleMapper triangle) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP,DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < triangle.length; i++) {
            bufferVertex(buffer,triangle.getOriginal());
            bufferVertex(buffer,triangle.getA(i));
            bufferVertex(buffer,triangle.getB(i));
        }
        Tessellator.getInstance().draw();
    }

    public void renderOutlines() {
        GlStateManager.color(1f-this.color[0],1f-this.color[1],1f-this.color[2],this.color[3]);
        for(TriangleMapper triangle : this.triangles)
            for(int i=0; i<triangle.length; i++)
                renderTriangleOutline(triangle,i);
    }

    public void renderTriangleOutline(TriangleMapper triangle, int index) {
        Vec3d og = triangle.getOriginal();
        Vec3d a = triangle.getA(index);
        Vec3d b = triangle.getB(index);
        GlStateManager.glBegin(GL11.GL_LINES);
        vertexFloat(og);
        vertexFloat(a);
        GlStateManager.glEnd();
        GlStateManager.glBegin(GL11.GL_LINES);
        vertexFloat(og);
        vertexFloat(b);
        GlStateManager.glEnd();
        GlStateManager.glBegin(GL11.GL_LINES);
        vertexFloat(a);
        vertexFloat(b);
        GlStateManager.glEnd();
    }

    private void vertexFloat(Vec3d vec) {
        GlStateManager.glVertex3f((float)vec.x,(float)vec.y,(float)vec.z);
    }

    private void bufferVertex(BufferBuilder buffer, Vec3d vec) {
        buffer.pos(vec.x,vec.y,vec.z).color(this.color[0],this.color[1],this.color[2],this.color[3]).endVertex();
    }
}
