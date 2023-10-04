package mods.thecomputerizer.sleepless.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderNightTerror {

    private final List<ModelRotatingBox> boxes;
    private final RenderManager manager;
    private final Random random;

    protected RenderNightTerror(RenderManager manager, Random random) {
        this.boxes = new ArrayList<>();
        this.manager = manager;
        this.random = random;
    }

    public void addBox() {
        this.boxes.add(new ModelRotatingBox(this.random));
    }

    public void render() {
    }
}
