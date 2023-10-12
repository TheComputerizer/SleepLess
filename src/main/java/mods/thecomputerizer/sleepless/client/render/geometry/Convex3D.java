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
    private final double[] translationOffset = new double[]{0d,0d,0d};
    private final double[] rotationSpeed = new double[]{0d,0d,0d};
    private final float[] currentRotation = new float[]{0f,0f,0f};
    private boolean showOutlines = true;
    private Vec3d previousRenderPos;
    private Vec3d orbitVec;
    private Orbit orbit;

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
            this.rotationSpeed[i] = newSpeed.length>i ? newSpeed[i] : 0d;
    }

    public void setRandomRotations(Random random, double speedFactor) {
        setRotationSpeed(random.nextDouble()*speedFactor,random.nextDouble()*speedFactor,random.nextDouble()*speedFactor);
    }

    public void setTranslationOffset(double ... newOffset) {
        for(int i=0; i<this.translationOffset.length; i++)
            this.translationOffset[i] = newOffset.length>i ? newOffset[i] : 0d;
    }

    public void setOrbit(double radius, double speed, double angle) {
        this.orbit = new Orbit(radius,speed,angle);
    }

    public void setRandomTranslationOffset(Random random, double range) {
        setTranslationOffset(randomOffset(random,range),randomOffset(random,range),randomOffset(random,range));
    }

    private double randomOffset(Random random,double range) {
        return (-range/2d)+(random.nextDouble()*range);
    }

    public void setEnableOutline(boolean showOutlines) {
        this.showOutlines = showOutlines;
    }

    private void preRender() {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER,0.003921569f);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
    }

    private void postRender() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(GL11.GL_GREATER,0.1f);
        GlStateManager.popMatrix();
    }

    public void render(double x, double y, double z) {
        render(new Vec3d(x,y,z));
    }

    public void render(Vec3d pos) {
        preRender();
        GlStateManager.color(this.color[0],this.color[1],this.color[2],this.color[3]);
        GlStateManager.scale(this.scale[0],this.scale[1],this.scale[2]);
        setTranslation(new Vec3d(pos.x/this.scale[0],pos.y/this.scale[1],pos.z/this.scale[2]));
        for(int i = 0; i < this.currentRotation.length; i++)
            this.currentRotation[i] = rotateClampedAxis(i);
        GlStateManager.rotate(this.currentRotation[0],1f,0f,0f);
        GlStateManager.rotate(this.currentRotation[1],0f,1f,0f);
        GlStateManager.rotate(this.currentRotation[2],0f,0f,1f);
        for(TriangleMapper triangle : this.triangles)
            renderTriangle(triangle);
        if(this.showOutlines) renderOutlines();
        postRender();
    }

    private void setTranslation(Vec3d initialPos) {
        if(Objects.isNull(this.previousRenderPos)) this.previousRenderPos = initialPos;
        if(Objects.isNull(this.orbit)) GlStateManager.translate(initialPos.x+this.translationOffset[0],
                initialPos.y+this.translationOffset[1],initialPos.z+this.translationOffset[2]);
        else {
            if(Objects.isNull(this.orbitVec))
                this.orbitVec = new Vec3d(this.translationOffset[0],this.translationOffset[1],this.translationOffset[2]);
            else if(!initialPos.equals(this.previousRenderPos)) {
                double distance = initialPos.distanceTo(this.previousRenderPos);
                this.orbitVec = this.orbitVec.add(initialPos.subtract(this.previousRenderPos).normalize().scale(distance));
            }
            this.orbitVec = this.orbit.getNextVec(this.orbitVec,initialPos);
            GlStateManager.translate(this.orbitVec.x,this.orbitVec.y,this.orbitVec.z);
        }
        this.previousRenderPos = initialPos;
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
