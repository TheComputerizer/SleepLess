package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
@SideOnly(Side.CLIENT)
public class ModelRotatingBox {

    private static final ResourceLocation TEST_RESOURCE = Constants.res("textures/entity/rotating_box.png");

    private final MutableInt renderTimer;
    private final TexturedQuad[] quads;
    private final float scale = 1f;
    private float xRotFactor;
    private float yRotFactor;
    private float zRotFactor;
    private boolean isIntialized;
    private float xRot;
    private float yRot;
    private float zRot;
    private Vec3d renderCenter;
    private Vec3d renderDims;
    private int quadIndex;
    private ModelRenderer testModel;

    public ModelRotatingBox(Random rand, float speedModifier, int renderTime) {
        this.renderTimer = new MutableInt(renderTime);
        this.quads = new TexturedQuad[6];
        this.xRotFactor = rand.nextFloat()*speedModifier;
        this.yRotFactor = rand.nextFloat()*speedModifier;
        this.zRotFactor = rand.nextFloat()*speedModifier;
        this.isIntialized = false;
    }

    public ModelRotatingBox setCenter(Vec3d center) {
        if(Objects.nonNull(center)) this.renderCenter = center;
        else Constants.LOGGER.error("A rotating box model cannot be given a null center!");
        return this;
    }

    public ModelRotatingBox setDimensions(Vec3d dimensions) {
        if(Objects.nonNull(dimensions)) {
            if(dimensions.x>0 && dimensions.y>0 && dimensions.z>0) this.renderDims = dimensions;
            else Constants.LOGGER.error("Tried to set invalid dimensions of ({}, {}, {}) to a rotating box" +
                    "model! Only dimensions greater than 0 are allowed!",dimensions.x,dimensions.y,dimensions.z);
        }
        else Constants.LOGGER.error("A rotating box model cannot be given null dimensions!");
        return this;
    }

    public ModelRotatingBox overrideRotations(Vec3d rotations) {
        if(Objects.nonNull(rotations)) {
            this.xRotFactor = (float)rotations.x;
            this.yRotFactor = (float)rotations.y;
            this.zRotFactor = (float)rotations.z;
        } else Constants.LOGGER.error("Cannot override rotations of rotating box model with a null vector!");
        return this;
    }

    public boolean init() {
        this.isIntialized = false;
        if(Objects.isNull(this.renderCenter) || Objects.isNull(this.renderDims))
            Constants.LOGGER.error("Failed to initialize a rotating box model!");
        else this.isIntialized = initQuads();
        if(this.isIntialized) addTestBox();
        return this.isIntialized;
    }

    private boolean initQuads() {
        this.quadIndex = 0;
        double xRadius = this.renderDims.x/2d;
        double yRadius = this.renderDims.y/2d;
        double zRadius = this.renderDims.z/2d;
        AxisAlignedBB box = new AxisAlignedBB(-xRadius,-yRadius,-zRadius,xRadius,yRadius,zRadius);
        return initQuadPair(box,true,false,false) && initQuadPair(box,false,true,false) &&
                initQuadPair(box,false,false,true);
    }

    private boolean initQuadPair(AxisAlignedBB box, boolean flipX, boolean flipY, boolean flipZ) {
        Vec3d adderVec = new Vec3d((flipX ? this.renderDims.x : 0d),(flipY ? this.renderDims.y : 0d),
                (flipZ ? this.renderDims.z : 0d));
        Vec3d[] corners = new Vec3d[]{getCorner(box,true,true,true),
                getCorner(box,true,!flipZ,flipZ),getCorner(box,flipX,!flipX,true),
                getCorner(box,flipX,flipY,flipZ)};
        if(!initQuad(corners)) return false;
        for(int i=0; i<corners.length; i++) corners[i] = corners[i].add(adderVec);
        return initQuad(corners);
    }

    private Vec3d getCorner(AxisAlignedBB box, boolean minX, boolean minY, boolean minZ) {
        return new Vec3d((minX ? box.minX : box.maxX),(minY ? box.minY : box.maxY),(minZ ? box.minZ : box.maxZ));
    }

    private boolean initQuad(Vec3d ... vertices) {
        if(vertices.length!=4) return false;
        try {
            this.quads[this.quadIndex] = new TexturedQuad(transformVertices(vertices));
            this.quadIndex++;
        } catch (ArrayIndexOutOfBoundsException ex) {
            Constants.LOGGER.error("Index {} is not a valid quad index!",this.quadIndex,ex);
            return false;
        }
        return true;
    }

    private PositionTextureVertex[] transformVertices(Vec3d ... vertices) {
        PositionTextureVertex[] textureVertices = new PositionTextureVertex[vertices.length];
        textureVertices[0] = new PositionTextureVertex(vertices[0],0f,0f);
        textureVertices[1] = new PositionTextureVertex(vertices[1],0f,1f);
        textureVertices[2] = new PositionTextureVertex(vertices[2],1f,0f);
        textureVertices[3] = new PositionTextureVertex(vertices[3],1f,1f);
        return textureVertices;
    }

    public void addTestBox() {
        ModelRenderer temp = new ModelRenderer(new ModelBiped(1f));
        temp.textureWidth = 64f;
        temp.textureHeight = 64f;
        temp.addBox(0f,0f,0f,8,8,8);
        this.testModel = temp;
    }

    public boolean onClientTick() {
        if(Objects.nonNull(this.renderCenter) && this.renderTimer.getValue()>0)
            return this.renderTimer.decrementAndGet()>0;
        return true;
    }

    public void render(@Nonnull RenderManager manager, @Nonnull BufferBuilder buffer, float partialTick) {
        if(Objects.nonNull(this.renderCenter)) {
            if(Objects.nonNull(this.testModel)) {
                //this.testModel.render(this.scale);
            }
            applyTranslations(partialTick);
            manager.renderEngine.bindTexture(TEST_RESOURCE);
            for(TexturedQuad quad : this.quads) quad.draw(buffer,this.scale);
        }
    }

    private void applyTranslations(float partialTick) {
        GlStateManager.translate(this.renderCenter.x,this.renderCenter.y,this.renderCenter.z);
        this.xRot = setClamped(this.xRot+(this.xRotFactor*partialTick),0f,360f);
        this.yRot = setClamped(this.yRot+(this.yRotFactor*partialTick),0f,360f);
        this.zRot = setClamped(this.zRot+(this.zRotFactor*partialTick),0f,360f);
        float max = this.xRot;
        if(this.yRot>max) max = this.yRot;
        if(this.zRot>max) max = this.zRot;
        float x = this.xRot/max;
        float y = this.yRot/max;
        float z = this.zRot/max;
        GlStateManager.rotate(1f,x,y,z);
    }

    private float setClamped(float val, float min, float max) {
        float dif = Math.abs(max-min);
        while(val>max) val-=dif;
        while(val<min) val+=dif;
        return val;
    }
}
