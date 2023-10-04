package mods.thecomputerizer.sleepless.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class ModelRotatingBox extends ModelRenderer {

    private final float xRotFactor;
    private final float yRotFactor;
    private final float zRotFactor;
    private float xRot;
    private float yRot;
    private float zRot;

    public ModelRotatingBox(Random rand) {
        super(null, "rotatingBox");
        this.xRotFactor = rand.nextFloat();
        this.yRotFactor = rand.nextFloat();
        this.zRotFactor = rand.nextFloat();
    }

    @SideOnly(Side.CLIENT)
    public void render(float scale) {
        super.render(scale);
    }
}
