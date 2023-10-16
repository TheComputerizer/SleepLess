package mods.thecomputerizer.sleepless.client.render.geometry;

import mods.thecomputerizer.sleepless.mixin.access.ModelRendererAccess;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelRendererCapture {

    private final ModelRenderer parentRenderer;
    private final List<ShapeHolder> boxes;
    private final List<ModelRendererCapture> childRenderers;
    private boolean isVisible = true;

    public ModelRendererCapture(ModelRenderer renderer, double modelScale) {
        this.parentRenderer = renderer;
        this.boxes = new ArrayList<>();
        convertBoxes(modelScale);
        this.childRenderers = new ArrayList<>();
        convertChildren(modelScale);
    }

    private void convertChildren(double modelScale) {
        if(Objects.nonNull(this.parentRenderer.childModels))
            for(ModelRenderer childRenderer : this.parentRenderer.childModels)
                this.childRenderers.add(new ModelRendererCapture(childRenderer,modelScale));
    }

    private void convertBoxes(double modelScale) {
        if(Objects.nonNull(this.parentRenderer.cubeList)) {
            for(ModelBox boxModel : this.parentRenderer.cubeList)
                this.boxes.add(ShapeHolder.fromModelBox(boxModel,modelScale,box -> {
                    box.setColor(0f, 0f, 0f, 0.5f, 1f, 1f, 1f, 1f);
                    box.setPushMatrix(false);
                    box.setEnableCull(true);
                },holder -> {}));
        }
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    private void renderInner(float scale, boolean renderChildren) {
        if(!this.parentRenderer.isHidden && this.parentRenderer.showModel) {
            GlStateManager.pushMatrix();
            for(ShapeHolder holder : this.boxes) holder.renderScaledRelative(scale);
            if(renderChildren)
                for(ModelRendererCapture childRenderer : this.childRenderers)
                    childRenderer.render(scale);
            GlStateManager.popMatrix();
        }
    }

    public void render(float scale) {
        if(this.isVisible) {
            GlStateManager.translate(this.parentRenderer.offsetX,this.parentRenderer.offsetY+scale,this.parentRenderer.offsetZ);
            if(hasNoRotationAngles()) {
                if(!hasRotationPoints()) renderInner(scale,true);
                else {
                    translateRotationPoint(scale,false);
                    renderInner(scale,true);
                    translateRotationPoint(scale,true);
                }
            } else {
                GlStateManager.pushMatrix();
                translateRotationPoint(scale,false);
                rotate();
                renderInner(scale,true);
                GlStateManager.popMatrix();
            }
            GlStateManager.translate(-this.parentRenderer.offsetX,-this.parentRenderer.offsetY-scale,-this.parentRenderer.offsetZ);
        } else {
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(true);
            this.parentRenderer.render(scale);
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(false);
        }
    }

    public void renderWithRotation(float scale) {
        if(this.isVisible) {
            GlStateManager.pushMatrix();
            translateRotationPoint(scale,false);
            rotate();
            renderInner(scale,false);
            GlStateManager.popMatrix();
        } else {
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(true);
            this.parentRenderer.renderWithRotation(scale);
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(false);
        }
    }

    public void postRender(float scale) {
        if(this.isVisible) {
            boolean hasNoRotationAngles = hasNoRotationAngles();
            if(!hasNoRotationAngles || hasRotationPoints()) translateRotationPoint(scale,false);
            if(!hasNoRotationAngles) rotate();
        } else {
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(true);
            this.parentRenderer.postRender(scale);
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(false);
        }
    }

    private boolean hasNoRotationAngles() {
        return this.parentRenderer.rotateAngleX==0f && this.parentRenderer.rotateAngleY==0f && this.parentRenderer.rotateAngleZ==0f;
    }

    private boolean hasRotationPoints() {
        return this.parentRenderer.rotationPointX!=0f || this.parentRenderer.rotationPointY!=0f || this.parentRenderer.rotationPointZ!=0f;
    }

    private void translateRotationPoint(float scale, boolean isReverse) {
        float reversal = isReverse ? scale*-1f : scale;
        GlStateManager.translate(this.parentRenderer.rotationPointX*reversal,
                this.parentRenderer.rotationPointY*reversal,this.parentRenderer.rotationPointZ*reversal);
    }

    private void rotate() {
        rotate(this.parentRenderer.rotateAngleZ,0f,0f,1f);
        rotate(this.parentRenderer.rotateAngleY,0f,1f,0f);
        rotate(this.parentRenderer.rotateAngleZ,1f,0f,0f);
    }

    private void rotate(float angle, float x, float y, float z) {
        if(angle!=0f) GlStateManager.rotate(angle*(180f/(float)Math.PI),x,y,z);
    }
}
