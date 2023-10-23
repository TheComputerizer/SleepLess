package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class Convex3D {

    private final double radius;
    private final TriangleMapper[] triangles;
    private final float[] color = new float[]{1f,1f,1f,1f,0f,0f,0f,0f};
    private final float[] scale = new float[]{1f,1f,1f};
    private final double[] translationOffset = new double[]{0d,0d,0d};
    private final double[] rotationSpeed = new double[]{0d,0d,0d};
    private final float[] currentRotation = new float[]{0f,0f,0f};
    private boolean showOutlines = true;
    private boolean enableCull = false;
    private boolean pushMatrix = true;
    private Vec3d previousRenderPos;
    private Vec3d orbitVec;
    private Orbit orbit;

    public Convex3D(Vec3d ... relativeCoords) {
        if(Objects.isNull(relativeCoords) || relativeCoords.length<=3)
            throw new RuntimeException("Only convex polygons with more than 3 vertices are supported for Convex3D objects");
        this.radius = relativeCoords[0].distanceTo(Vec3d.ZERO);
        this.triangles = new TriangleMapper[relativeCoords.length];
        for(int i=0; i<relativeCoords.length; i++)
            this.triangles[i] = new TriangleMapper(relativeCoords[i],relativeCoords);
    }

    public double getRadius() {
        return this.radius;
    }

    public double getScaledHeight() {
        return this.radius/this.scale[1];
    }

    public void setColor(float ... newColor) {
        for(int i=0; i<4; i++) {
            this.color[i] = newColor.length>i ? newColor[i] : 1f;
            int oi = i+4; //outline index
            this.color[oi] = newColor.length>oi ? newColor[oi] : (i<3 ? 1f-this.color[i] : this.color[i]);
        }
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

    public void setEnableCull(boolean disableCull) {
        this.enableCull = disableCull;
    }

    public void setPushMatrix(boolean pushMatrix) {
        this.pushMatrix = pushMatrix;
    }

    private void preRender() {
        if(this.pushMatrix) GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if(!this.enableCull) GlStateManager.depthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER,0.003921569f);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
    }

    private void postRender() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        if(!this.enableCull) GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(GL11.GL_GREATER,0.1f);
        if(this.pushMatrix) GlStateManager.popMatrix();
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
        GlStateManager.color(this.color[4],this.color[5],this.color[6],this.color[7]);
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
