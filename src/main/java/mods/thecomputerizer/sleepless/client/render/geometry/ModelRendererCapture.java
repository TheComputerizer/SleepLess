package mods.thecomputerizer.sleepless.client.render.geometry;

import mods.thecomputerizer.sleepless.mixin.access.ModelRendererAccess;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class ModelRendererCapture {

    private final ModelRenderer parentRenderer;
    private final List<ShapeHolder> boxes;
    private final List<ModelRendererCapture> childRenderers;
    public ModelPartAnimation additionalAnimations;
    private boolean isVisible = true;
    private float translationScale = 1f;
    private Vec3d translationDirection = Vec3d.ZERO;

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

    public void setAdditionalTranslations(float scale, Vec3d dirVec) {
        this.translationScale = scale;
        this.translationDirection = dirVec;
        for(ModelRendererCapture child : this.childRenderers)
            child.setAdditionalTranslations(scale, dirVec);
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    private void renderInner(float scale, boolean renderChildren) {
        if(!this.parentRenderer.isHidden && this.parentRenderer.showModel) {
            GlStateManager.pushMatrix();
            for(ShapeHolder holder : this.boxes) holder.renderScaledRelative(this.translationDirection,scale*this.translationScale);
            if(renderChildren)
                for(ModelRendererCapture childRenderer : this.childRenderers)
                    childRenderer.render(scale);
            GlStateManager.popMatrix();
        }
    }

    public void render(float scale) {
        if(Objects.nonNull(this.additionalAnimations))
            this.additionalAnimations.applyAnimations(this.parentRenderer,this.isVisible,scale);
        if(this.isVisible) {
            GlStateManager.translate(this.parentRenderer.offsetX,this.parentRenderer.offsetY,this.parentRenderer.offsetZ);
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
            GlStateManager.translate(-this.parentRenderer.offsetX,-this.parentRenderer.offsetY,-this.parentRenderer.offsetZ);
        } else {
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(true);
            this.parentRenderer.render(scale);
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(false);
        }
        if(Objects.nonNull(this.additionalAnimations))
            this.additionalAnimations.resetAnimations(this.parentRenderer,this.isVisible);
    }

    public void renderWithRotation(float scale) {
        if(Objects.nonNull(this.additionalAnimations))
            this.additionalAnimations.applyAnimations(this.parentRenderer,this.isVisible,scale);
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
        if(Objects.nonNull(this.additionalAnimations))
            this.additionalAnimations.resetAnimations(this.parentRenderer,this.isVisible);
    }

    public void postRender(float scale) {
        if(Objects.nonNull(this.additionalAnimations))
            this.additionalAnimations.applyAnimations(this.parentRenderer,this.isVisible,scale);
        if(this.isVisible) {
            boolean hasNoRotationAngles = hasNoRotationAngles();
            if(!hasNoRotationAngles || hasRotationPoints()) translateRotationPoint(scale,false);
            if(!hasNoRotationAngles) rotate();
        } else {
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(true);
            this.parentRenderer.postRender(scale);
            ((ModelRendererAccess)this.parentRenderer).sleepless$setSkipCapture(false);
        }
        if(Objects.nonNull(this.additionalAnimations))
            this.additionalAnimations.resetAnimations(this.parentRenderer,this.isVisible);
    }

    private boolean hasNoRotationAngles() {
        ModelRenderer parent = this.parentRenderer;
        return parent.rotateAngleX==0f && parent.rotateAngleY==0f && parent.rotateAngleZ==0f;
    }

    private boolean hasRotationPoints() {
        ModelRenderer parent = this.parentRenderer;
        return parent.rotationPointX!=0f || parent.rotationPointY!=0f || parent.rotationPointZ!=0f;
    }

    private void translateRotationPoint(float scale, boolean isReverse) {
        ModelRenderer parent = this.parentRenderer;
        Vec3d translated = new Vec3d(parent.rotationPointX,parent.rotationPointY,parent.rotationPointZ)
                .scale(isReverse ? scale*-1f : scale);
        GlStateManager.translate(translated.x,translated.y,translated.z);
    }

    private void rotate() {
        ModelRenderer parent = this.parentRenderer;
        rotate(parent.rotateAngleZ,0f,0f,1f);
        rotate(parent.rotateAngleY,0f,1f,0f);
        rotate(parent.rotateAngleZ,1f,0f,0f);
    }

    private void rotate(float angle, float x, float y, float z) {
        if(angle!=0f) GlStateManager.rotate(angle*(180f/(float)Math.PI),x,y,z);
    }

    public static class ModelPartAnimation {

        private boolean isMirrored = false;
        private boolean onlyWhenVisible = false;
        private boolean isRotationAdder = true;
        private Vec3d rotations = Vec3d.ZERO;
        private double offsetRadius = 0d;
        private Vec3d offsetDirection = Vec3d.ZERO;
        private Vec3d previousRotations = Vec3d.ZERO;
        private Vec3d previousRotationPoints = Vec3d.ZERO;

        public ModelPartAnimation setOnlyWhenVisible(boolean onlyWhenVisible) {
            this.onlyWhenVisible = onlyWhenVisible;
            return this;
        }

        public ModelPartAnimation setMirrored(boolean isMirrored) {
            this.isMirrored = isMirrored;
            return this;
        }

        public ModelPartAnimation setRotationDegrees(double x, double y, double z) {
            return setRotationDegrees(true,x,y,z);
        }

        public ModelPartAnimation setRotationDegrees(boolean isAdder, double x, double y, double z) {
            return setRotations(isAdder,new Vec3d(Math.toRadians(x),Math.toRadians(y),Math.toRadians(z)));
        }

        public ModelPartAnimation setRotationDegrees(boolean isAdder, Vec3d rotations) {
            return setRotations(isAdder,new Vec3d(Math.toRadians(rotations.x),Math.toRadians(rotations.y),Math.toRadians(rotations.z)));
        }

        /**
         * The rotation angles here need to be in radians
         */
        public ModelPartAnimation setRotations(boolean isAdder, Vec3d rotations) {
            this.isRotationAdder = isAdder;
            this.rotations = rotations;
            return this;
        }

        public ModelPartAnimation setOffset(double radius) {
            return setOffset(radius,Vec3d.ZERO);
        }

        public ModelPartAnimation setOffset(double radius, double x, double y, double z) {
            return setOffset(radius,new Vec3d(x,y,z));
        }

        public ModelPartAnimation setOffset(double radius, Vec3d direction) {
            this.offsetRadius = radius;
            this.offsetDirection = direction;
            return this;
        }

        public ModelPartAnimation reset() {
            this.onlyWhenVisible = false;
            this.isRotationAdder = true;
            this.rotations = Vec3d.ZERO;
            this.offsetRadius = 1d;
            this.offsetDirection = Vec3d.ZERO;
            return this;
        }

        private void applyAnimations(ModelRenderer part, boolean isVisible, float scale) {
            if(isVisible || !this.onlyWhenVisible) {
                this.previousRotations = new Vec3d(part.rotateAngleX,part.rotateAngleY,part.rotateAngleZ);
                Vec3d mirroredRotations = this.isMirrored ? this.rotations.scale(-1d) : this.rotations;
                part.rotateAngleX = rotateAxis(part.rotateAngleX,(float)mirroredRotations.x);
                part.rotateAngleY = rotateAxis(part.rotateAngleY,(float)mirroredRotations.y);
                part.rotateAngleZ = rotateAxis(part.rotateAngleZ,(float)mirroredRotations.z);
                this.previousRotationPoints = new Vec3d(part.rotationPointX,part.rotationPointY,part.rotationPointZ);
                Vec3d scaledRotationPoints = (this.isMirrored ? this.offsetDirection.scale(-scale) :
                        this.offsetDirection.scale(scale)).add(this.previousRotationPoints.scale(this.offsetRadius));
                part.rotationPointX = (float)scaledRotationPoints.x;
                part.rotationPointY = (float)scaledRotationPoints.y;
                part.rotationPointZ = (float)scaledRotationPoints.z;
            }
        }

        private float rotateAxis(float original, float extra) {
            return this.isRotationAdder ? original+extra : extra;
        }

        private void resetAnimations(ModelRenderer part, boolean isVisible) {
            if(isVisible || !this.onlyWhenVisible) {
                part.rotateAngleX = (float)this.previousRotations.x;
                part.rotateAngleY = (float)this.previousRotations.y;
                part.rotateAngleZ = (float)this.previousRotations.z;
                part.rotationPointX = (float)this.previousRotationPoints.x;
                part.rotationPointY = (float)this.previousRotationPoints.y;
                part.rotationPointZ = (float)this.previousRotationPoints.z;
            }
        }
    }
}
