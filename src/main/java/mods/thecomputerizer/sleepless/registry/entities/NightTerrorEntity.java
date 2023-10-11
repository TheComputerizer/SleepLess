package mods.thecomputerizer.sleepless.registry.entities;

import mods.thecomputerizer.sleepless.client.render.ModelRotatingBox;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class NightTerrorEntity extends EntityLiving {

    @SideOnly(Side.CLIENT)
    private ModelRotatingBox testBox;

    public NightTerrorEntity(World world) {
        super(world);
    }

    @SideOnly(Side.CLIENT)
    public ModelRotatingBox getTestBox(double x, double y, double z) {
        if(Objects.isNull(this.testBox)) {
            this.testBox = new ModelRotatingBox(this.rand,5f,-1).setCenter(new Vec3d(x, y, z))
                    .setDimensions(new Vec3d(2d, 2d, 2d));
            return this.testBox.init() ? this.testBox : null;
        }
        this.testBox.setCenter(new Vec3d(x, y, z));
        return this.testBox;
    }
}
