package mods.thecomputerizer.sleepless.registry.entities;

import mods.thecomputerizer.sleepless.client.render.geometry.Cube;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class NightTerrorEntity extends EntityLiving {

    @SideOnly(Side.CLIENT)
    private Cube testCube;

    public NightTerrorEntity(World world) {
        super(world);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public Cube getTestCube() {
        if(Objects.isNull(this.testCube)) {
            this.testCube = new Cube();
            this.testCube.setRandomRotations(this.rand,5d);
            this.testCube.setColor(1f,1f,1f,0.5f);
        }
        return this.testCube;
    }
}
