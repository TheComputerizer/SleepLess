package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.util.math.Vec3d;

public class Cube extends Convex3D {

    public Cube() {
        super(new Vec3d(0.5d,-0.5d,-0.5d),new Vec3d(0.5d,0.5d,-0.5d),
                new Vec3d(0.5d,-0.5d,0.5d),new Vec3d(0.5d,0.5d,0.5d),
                new Vec3d(-0.5d,-0.5d,-0.5d),new Vec3d(-0.5d,0.5d,-0.5d),
                new Vec3d(-0.5d,-0.5d,0.5d),new Vec3d(-0.5d,0.5d,0.5d));
    }
}
