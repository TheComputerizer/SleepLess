package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class PhaseAction {

    private final Type type;
    private int time;
    private long animationOffset;
    private PhaseAction nextAction;
    private boolean invertInvul = false;

    public PhaseAction(NBTTagCompound tag) {
        this.type = Type.BY_NAME.get(tag.getString("actionType"));
        this.time = tag.getInteger("actionTime");
        this.invertInvul = tag.getBoolean("invertInvulnerability");
    }

    public PhaseAction(Type type, int time) {
        this.type = type;
        this.time = time;
        this.animationOffset = calculateAnimationOffset();
    }

    private long calculateAnimationOffset() {
        NightTerrorEntity.AnimationType animation = this.type.animationType;
        return Objects.isNull(animation) || this.time>=animation.getTotalTime() ? 0L : animation.getTotalTime()-this.time;
    }

    public PhaseAction setNextAction(PhaseAction action) {
        this.nextAction = action;
        return this;
    }

    public PhaseAction setInvertInvulnerability() {
        this.invertInvul = true;
        return this;
    }

    public PhaseAction getNextAction() {
        return this.nextAction;
    }

    public PhaseAction getTickedAction() {
        return this.time-->0 ? this : this.nextAction;
    }

    public void onStart(NightTerrorEntity entity) {
        this.type.onStart(entity,this.animationOffset,this.invertInvul);
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("actionType",this.type.name);
        tag.setInteger("actionTime",this.time);
        tag.setBoolean("invertInvulnerability",this.invertInvul);
    }

    public enum Type {

        DAMAGE("damage",true,NightTerrorEntity.AnimationType.DAMAGE,entity -> {},entity -> {}),
        FLOAT("float",false,null,entity -> {},entity -> {}),
        SPAWN("spawn",true,NightTerrorEntity.AnimationType.SPAWN,entity -> {},entity -> {}),
        TELEPORT("teleport",true,NightTerrorEntity.AnimationType.TELEPORT,
                entity -> SoundUtil.playRemoteEntitySound(entity,SoundRegistry.BOOSTED_TP_SOUND,false,1f,0.5f),
                entity -> SoundUtil.playRemoteEntitySound(entity,SoundRegistry.BOOSTED_TP_REVERSE_SOUND,false,1f,0.5f)),
        TRANSITION("transition",true,null,entity -> {},entity -> {}),
        WAIT("wait",false,NightTerrorEntity.AnimationType.IDLE,entity -> {},entity -> {});

        private static final Map<String,Type> BY_NAME = new HashMap<>();

        static {
            for(Type type : Type.values()) BY_NAME.put(type.name,type);
        }

        private final String name;
        private final boolean isInvulnerable;
        private final @Nullable NightTerrorEntity.AnimationType animationType;
        private final Consumer<NightTerrorEntity> extraStartFunc;
        private final Consumer<NightTerrorEntity> extraFinishFunc;
        Type(String name, boolean isInvulnerable, @Nullable NightTerrorEntity.AnimationType animationType,
             Consumer<NightTerrorEntity> extraStartFunc, Consumer<NightTerrorEntity> extraFinishFunc) {
            this.name = name;
            this.isInvulnerable = isInvulnerable;
            this.animationType = animationType;
            this.extraStartFunc = extraStartFunc;
            this.extraFinishFunc = extraStartFunc;
        }

        public void onStart(NightTerrorEntity entity, long animationOffset, boolean invertInvul) {
            entity.setEntityInvulnerable(invertInvul != this.isInvulnerable);
            if(Objects.nonNull(this.animationType))
                entity.setAnimation(this.animationType,animationOffset);
            this.extraStartFunc.accept(entity);
        }

        public void onFinish(NightTerrorEntity entity) {
            this.extraFinishFunc.accept(entity);
        }
    }
}
