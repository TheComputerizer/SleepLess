package mods.thecomputerizer.sleepless.mixin.access;

import mods.thecomputerizer.sleepless.client.render.geometry.ModelRendererCapture;

public interface ModelRendererAccess {

    void sleepless$setCapture(ModelRendererCapture capture);
    ModelRendererCapture sleepless$getCapture();
    void sleepless$setSkipCapture(boolean shouldSkip);
}
