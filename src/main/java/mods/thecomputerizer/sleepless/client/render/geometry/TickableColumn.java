package mods.thecomputerizer.sleepless.client.render.geometry;

import mods.thecomputerizer.sleepless.client.ClientEvents;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class TickableColumn extends Column implements ITickableGeometry<TickableColumn> {

    private boolean isInitialized = false;
    private int maxTime;
    private int time;

    public TickableColumn(Random random, Vec3d relativeBottom, double height, double radius, double spacing) {
        super(random, relativeBottom, height, radius, spacing);
    }

    @Override
    public void render(Vec3d relativeCenter) {
        if(this.isInitialized) super.render(relativeCenter);
    }

    @Override
    public TickableColumn setTime(int time) {
        this.maxTime = time;
        this.time = time;
        return this;
    }

    @Override
    public TickableColumn init() {
        this.isInitialized = true;
        ClientEvents.TICKABLE_GEOMETRY.add(this);
        return this;
    }

    @Override
    public boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public void onTick() {
        if(this.time--<=0) reset();
    }

    @Override
    public TickableColumn reset() {
        this.time = this.maxTime;
        this.isInitialized = false;
        return this;
    }
}
