package mods.thecomputerizer.sleepless.registry.entities.nightterror;

import mods.thecomputerizer.sleepless.registry.entities.ai.ExtendedMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;
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

    @Override
    public void onUpdateMoveHelper() {
        NightTerrorEntity terror = getEntity();
        if(this.action == EntityMoveHelper.Action.MOVE_TO) {
            double motionX = getHorizontalOffset(true,terror.posX,terror.posZ);
            double motionY = this.posY-terror.posY;
            double motionZ = getHorizontalOffset(false,terror.posX,terror.posZ);
            if(Math.pow(motionX,2)+Math.pow(motionY,2)+Math.pow(motionZ,2)<0.5d) {
                terror.setMoveVertical(0f);
                terror.setMoveForward(0f);
                setAction(Action.WAIT);
                return;
            }
            float targetYaw = (float)(MathHelper.atan2(motionZ,motionX)*(180d/Math.PI))-90f;
            terror.rotationYaw = this.limitAngle(this.entity.rotationYaw,targetYaw,180f);
            double entitySpeed = getEntitySpeed(terror);
            terror.setAIMoveSpeed((float)(this.speed*entitySpeed));
            double xzHypot = MathHelper.sqrt(Math.pow(motionX,2)+Math.pow(motionZ,2));
            float targetPitch = (float)(-(MathHelper.atan2(motionY,xzHypot)*(180d/Math.PI)));
            this.entity.rotationPitch = this.limitAngle(this.entity.rotationPitch,targetPitch,10.0f);
            this.entity.setMoveVertical(motionY>0d ? (float)entitySpeed : (float)-entitySpeed);
        } else {
            terror.setMoveVertical(0f);
            terror.setMoveForward(0f);
        }
    }
}
