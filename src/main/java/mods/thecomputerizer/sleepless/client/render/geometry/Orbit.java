package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Orbit {

    private final double radius;
    private final double speed;
    /**
     * The angle is stored in radians and follows the right hand rule
     */
    private final double angle;

    public Orbit(double radius, double speed, double angle) {
        this.radius = radius;
        this.speed = speed;
        this.angle = angle;
    }

    public Vec3d getNextVec(Vec3d curVec, Vec3d centerVec) {
        double curDistance = curVec.distanceTo(centerVec);
        double curGravity = curDistance<this.radius ? 0d : this.speed/(this.radius/curDistance);
        if(curGravity>this.speed) curGravity = ((curGravity-this.speed)/100d)+this.speed;
        Vec3d dirVec = curVec.crossProduct(centerVec).normalize().scale(this.speed);
        Vec3d gravityVec = centerVec.subtract(curVec).normalize().scale(curGravity);
        return curVec.add(dirVec).add(gravityVec);
    }
}
