package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomEntity;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;

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

    public PhaseAction setTeleportTarget(NightTerrorEntity entity, double x, double y, double z, boolean isOffset) {
        entity.setTeleportTarget(x,y,z,isOffset);
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

        DAMAGE("damage",true,NightTerrorEntity.AnimationType.DAMAGE,entity -> {
            if(!entity.world.isRemote) {
                entity.setTeleportTarget(0d,20d,0d,true);
                int numSpawns = Math.max(1, (int) ((1f-(entity.getHealth()/entity.getMaxHealth()))*10f));
                BlockPos pos = entity.getPosition();
                for (int i = 0; i < numSpawns; i++) {
                    final BlockPos finalPos = new BlockPos(pos);
                    PhantomEntity.spawnPhantom(entity.getEntityWorld(), phantom -> {
                        phantom.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6d);
                        phantom.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3d);
                        phantom.setGlowing(true);
                        phantom.markAggressive();
                        phantom.setLifespan(600);
                        phantom.presetClass(NightTerrorEntity.class);
                        phantom.setPosition(finalPos.getX(), finalPos.getY(), finalPos.getZ());
                    });
                    pos = pos.add(0, 3, 0);
                }
            }
        },entity -> {}),
        FLOAT("float",false,null,entity -> {},entity -> {}),
        SPAWN("spawn",true,null,entity -> {
            if(!entity.world.isRemote) {
                int numSpawns = Math.max(3,(int)((1f-(entity.getHealth()/entity.getMaxHealth()))*20f));
                BlockPos pos = entity.getPosition();
                for (int i = 0; i < numSpawns; i++) {
                    final BlockPos finalPos = new BlockPos(pos);
                    PhantomEntity.spawnPhantom(entity.getEntityWorld(), phantom -> {
                        phantom.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6d);
                        phantom.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3d);
                        phantom.setGlowing(true);
                        phantom.markAggressive();
                        phantom.setLifespan(600);
                        phantom.presetClass(NightTerrorEntity.class);
                        phantom.setPosition(finalPos.getX(), finalPos.getY(), finalPos.getZ());
                    });
                    pos = pos.add(0, 3, 0);
                }
            }
        },entity -> {}),
        TELEPORT("teleport",true,NightTerrorEntity.AnimationType.TELEPORT,
                entity -> {
                    entity.addPotionEffect(new PotionEffect(PotionRegistry.PHASED,50));
                    entity.setMoveTarget(1d);
                    SoundUtil.playRemoteEntitySound(entity,SoundRegistry.BOOSTED_TP_SOUND,false,1f,0.5f);
                },
                entity -> SoundUtil.playRemoteEntitySound(entity,SoundRegistry.BOOSTED_TP_REVERSE_SOUND,false,1f,0.5f)),
        TRANSITION("transition",true,null,entity -> {},entity -> {}),
        WAIT("wait",false,NightTerrorEntity.AnimationType.SPAWN,entity -> {
            EntityLivingBase target = entity.getAttackTarget();
            if(Objects.nonNull(target)) entity.setTeleportTarget(target.posX+1,target.posY,target.posZ,false);
        },entity -> {});

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
            boolean invul = invertInvul != this.isInvulnerable;
            entity.setEntityInvulnerable(invul);
            entity.setGlowing(!invul);
            if(entity.world.isRemote && entity.getAnimationData().currentAnimation==NightTerrorEntity.AnimationType.IDLE && !invul)
                entity.renderMode = 2;
            if(Objects.nonNull(this.animationType))
                entity.setAnimation(this.animationType,animationOffset);
            this.extraStartFunc.accept(entity);
        }

        public void onFinish(NightTerrorEntity entity) {
            this.extraFinishFunc.accept(entity);
        }
    }
}
