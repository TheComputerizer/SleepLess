package mods.thecomputerizer.sleepless.registry.entities.nightterror;

import mods.thecomputerizer.sleepless.registry.entities.ai.ExtendedMoveHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class NightTerrorMoveHelper extends ExtendedMoveHelper<NightTerrorEntity> {

    public NightTerrorMoveHelper(NightTerrorEntity terror) {
        super(terror);
    }

    public void setCurvedMove(Vec3d targetVec, Random rand) {
        NightTerrorEntity terror = getEntity();
        Vec3d terrorPosVec = terror.getPositionVector();
        Vec3d dirVec = targetVec.subtract(terrorPosVec).normalize();
        Vec3d curveDirVec = terrorPosVec.crossProduct(targetVec).normalize();
        double curveStrength = rand.nextDouble()*2d;
        if(curveStrength<1d) curveStrength = -(curveStrength+1d);
        curveDirVec.scale(curveStrength);
        setEntityMotion(curveDirVec,true);
    }
}
