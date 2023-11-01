package mods.thecomputerizer.sleepless.util;

import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class VectorRandomizer {

    private final Random rand;
    private final Vec3d minOffset;
    private final Vec3d maxOffset;
    public VectorRandomizer(Random rand, double minXOffset, double minYOffset, double minZOffset, double maxXOffset,
                            double maxYOffset, double maxZOffset) {
        this(rand,new Vec3d(minXOffset,minYOffset,minZOffset),new Vec3d(maxXOffset,maxYOffset,maxZOffset));
    }

    public VectorRandomizer(Random rand, Vec3d minOffset, Vec3d maxOffset) {
        this.rand = rand;
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
    }

    public Vec3d rollOffset(Vec3d originVec) {
        double x = rollDouble(this.minOffset.x,this.maxOffset.x);
        double y = rollDouble(this.minOffset.y,this.maxOffset.y);
        double z = rollDouble(this.minOffset.z,this.maxOffset.z);
        return originVec.add(x,y,z);
    }

    private double rollDouble(double min, double max) {
        return min+(this.rand.nextDouble()*(max-min));
    }
}
