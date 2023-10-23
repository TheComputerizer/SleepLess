package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public enum Shapes {

    BOX(() -> new Vec3d[]{new Vec3d(0.5d,-0.5d,-0.5d),new Vec3d(0.5d,0.5d,-0.5d),
                new Vec3d(0.5d,-0.5d,0.5d),new Vec3d(0.5d,0.5d,0.5d),
                new Vec3d(-0.5d,-0.5d,-0.5d),new Vec3d(-0.5d,0.5d,-0.5d),
                new Vec3d(-0.5d,-0.5d,0.5d),new Vec3d(-0.5d,0.5d,0.5d)});

    private final Supplier<Vec3d[]> vectorSupplier;

    Shapes(Supplier<Vec3d[]> vectorSupplier) {
        this.vectorSupplier = vectorSupplier;
    }

    public Convex3D makeInstance() {
        return new Convex3D(this.vectorSupplier.get());
    }
}
