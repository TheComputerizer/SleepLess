package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * This is basically just a better implementation of Map<Vec3d,Collection<Tuple<Vec3d,Vec3d>>>
 */
public class TriangleMapper {

    private final Vec3d original;
    private final Vec3d[] pairA;
    private final Vec3d[] pairB;
    public final int length;

    public TriangleMapper(Vec3d original, Vec3d ... otherVectors) {
        this.original = original;
        Vec3d[] potentialClosest = findClosest(otherVectors);
        this.length = potentialClosest.length-1;
        this.pairA = new Vec3d[this.length];
        this.pairB = new Vec3d[this.length];
        calculatePairs(potentialClosest);
    }

    private Vec3d[] findClosest(Vec3d ... otherVectors) {
        List<Vec3d> firstClosest = new ArrayList<>();
        List<Vec3d> secondClosest = new ArrayList<>();
        double firstDist = Double.MAX_VALUE;
        double secondDist = Double.MAX_VALUE;
        for(Vec3d otherVec : otherVectors) {
            if(otherVec!=this.original) {
                double distance = this.original.distanceTo(otherVec);
                if(firstDist==Double.MAX_VALUE || isCloseEnough(firstDist,distance)) {
                    firstClosest.add(otherVec);
                    firstDist = distance;
                } else if(secondDist==Double.MAX_VALUE || isCloseEnough(secondDist,distance)) {
                    secondClosest.add(otherVec);
                    secondDist = distance;
                }
            }
        }
        if(firstClosest.size()<2) firstClosest.addAll(secondClosest);
        return firstClosest.toArray(new Vec3d[0]);
    }

    private void calculatePairs(Vec3d ... potentialClosest) {
        for(int i=0; i<potentialClosest.length-1; i++) {
            if(i==potentialClosest.length-2) {
                this.pairA[i] = potentialClosest[i];
                this.pairB[i] = potentialClosest[i+1];
            } else {
                int match = i+1;
                double minDist = Double.MAX_VALUE;
                for(int j=i+1; j<potentialClosest.length; j++) {
                    double distance = potentialClosest[i].distanceTo(potentialClosest[j]);
                    if(minDist==Double.MAX_VALUE || isCloseEnough(minDist,distance)) {
                        match = j;
                        minDist = distance;
                    }
                }
                this.pairA[i] = potentialClosest[i];
                this.pairB[i] = potentialClosest[match];
            }
        }
    }

    private boolean isCloseEnough(double min, double distance) {
        return ((int)(distance*200d))<=((int)(min*200d));
    }

    public Vec3d getOriginal() {
        return this.original;
    }

    public Vec3d getA(int index) {
        return this.pairA[index];
    }

    public Vec3d getB(int index) {
        return this.pairB[index];
    }
}
