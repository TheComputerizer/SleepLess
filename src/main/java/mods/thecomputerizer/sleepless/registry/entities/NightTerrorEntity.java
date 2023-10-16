package mods.thecomputerizer.sleepless.registry.entities;

import mods.thecomputerizer.sleepless.client.render.geometry.Column;
import mods.thecomputerizer.sleepless.client.render.geometry.Convex3D;
import mods.thecomputerizer.sleepless.client.render.geometry.Shapes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class NightTerrorEntity extends EntityLiving {

    @SideOnly(Side.CLIENT)
    private Convex3D testCube;

    @SideOnly(Side.CLIENT)
    private Column testColumn;

    public NightTerrorEntity(World world) {
        super(world);
        this.ignoreFrustumCheck = true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public Convex3D getTestCube() {
        if(Objects.isNull(this.testCube)) {
            this.testCube = Shapes.BOX.makeInstance();
            //this.testCube.setRotationSpeed(this.rand.nextDouble(),this.rand.nextDouble());
            this.testCube.setRandomRotations(this.rand,1d);
            //this.testCube.setRandomTranslationOffset(this.rand,1d);
            this.testCube.setTranslationOffset(0d,0d,15d);
            this.testCube.setOrbit(5d,0.04d,0d);
            this.testCube.setColor(0f,0f,0f,0.15f);
            this.testCube.setScale(0.25f,0.25f,0.25f);
        }
        return this.testCube;
    }

    @SideOnly(Side.CLIENT)
    public Column getTestColumn() {
        if(Objects.isNull(this.testColumn))
            this.testColumn = new Column(this.rand,new Vec3d(10d,-10d,0d),1000d,10d,7.5d);
        return this.testColumn;
    }
}
