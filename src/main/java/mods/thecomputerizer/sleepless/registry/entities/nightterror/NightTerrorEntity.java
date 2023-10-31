package mods.thecomputerizer.sleepless.registry.entities.nightterror;

import mcp.MethodsReturnNonnullByDefault;
import mods.thecomputerizer.sleepless.client.render.geometry.ShapeHolder;
import mods.thecomputerizer.sleepless.client.render.geometry.Shapes;
import mods.thecomputerizer.sleepless.config.SleepLessConfig;
import mods.thecomputerizer.sleepless.registry.DataSerializerRegistry;
import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import mods.thecomputerizer.sleepless.registry.entities.ai.EntityWatchClosestWithSleepDebt;
import mods.thecomputerizer.sleepless.registry.entities.ai.PhantomNearestAttackableTarget;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseBase;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.phase.PhaseSpawn;
import mods.thecomputerizer.theimpossiblelibrary.util.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NightTerrorEntity extends EntityCreature {

    private static final DataParameter<AnimationData> ANIMATION_SYNC = EntityDataManager.createKey(NightTerrorEntity.class,
            (DataSerializer<AnimationData>)DataSerializerRegistry.ANIMATION_SERIALIZER.getSerializer());

    @SideOnly(Side.CLIENT)
    public int renderMode = 0;

    private long ticksAlive;
    private PhaseBase currentPhase;
    private Vec3d teleportTarget;

    public NightTerrorEntity(World world) {
        super(world);
        this.ignoreFrustumCheck = true;
        this.moveHelper = new NightTerrorMoveHelper(this);
        this.currentPhase = new PhaseSpawn(this,1f);
        this.experienceValue = 100;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64d);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50d);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5d);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ANIMATION_SYNC,new AnimationData());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(6,new EntityWatchClosestWithSleepDebt(this,64f,SleepLessConfig.NIGHT_TERROR.minSleepDebt,1f));
        this.targetTasks.addTask(1,new PhantomNearestAttackableTarget<>(this, EntityPlayer.class,
                1,false,false,7f,null));
    }

    @Override
    public float getWaterSlowDown() {
        return 1f;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void playStepSound(BlockPos pos, Block block) {}

    public long getTicksAlive() {
        return this.ticksAlive;
    }

    public void setTeleportTarget(double x, double y, double z, boolean isOffset) {
        this.teleportTarget = isOffset ? this.getPositionVector().add(x,y,z) : new Vec3d(x,y,z);
    }

    public void setMoveTarget(double speed) {
        if(Objects.isNull(this.teleportTarget)) this.teleportTarget = this.getPositionVector();
        this.moveHelper.setMoveTo(this.teleportTarget.x,this.teleportTarget.y,this.teleportTarget.z,speed);
    }

    public AnimationData getAnimationData() {
        return this.dataManager.get(ANIMATION_SYNC);
    }

    public void setAnimation(AnimationType type, long offset) {
        getAnimationData().setAnimation(type,offset);
        this.dataManager.setDirty(ANIMATION_SYNC);
    }

    public void setPhase(PhaseBase phase) {
        this.currentPhase = phase;
    }

    public boolean isNotDying() {
        return this.getAnimationData().currentAnimation!=AnimationType.DEATH;
    }

    @Override
    public void onLivingUpdate() {
        if(!this.dead) {
            this.ticksAlive++;
            this.currentPhase.onTick();
            this.dataManager.get(ANIMATION_SYNC).tickAnimations(this);
        }
        super.onLivingUpdate();
    }

    @Override
    protected void damageEntity(DamageSource source, float damageAmount) {
        if(this.dataManager.get(ANIMATION_SYNC).currentAnimation==AnimationType.DEATH) return;
        super.damageEntity(source,damageAmount);
        if(this.getHealth()<=0f && this.currentPhase.canDie()) {
            this.setAnimation(AnimationType.DEATH,0L);
            this.setHealth(0.01f);
        } else this.currentPhase.onDamage();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setLong("nightTerrorTicksAlive",this.ticksAlive);
        tag.setTag("nightTerrorAnimationData",this.dataManager.get(ANIMATION_SYNC).writeToNBT());
        tag.setTag("nightTerrorPhaseInfo",this.currentPhase.writeToNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.ticksAlive = tag.getLong("nightTerrorTicksAlive");
        this.dataManager.get(ANIMATION_SYNC).readFromNBT(tag.getCompoundTag("nightTerrorAnimationData"));
        this.dataManager.setDirty(ANIMATION_SYNC);
    }

    @Override
    protected void onDeathUpdate() {
        this.deathTime = Math.max(this.deathTime,19);
        super.onDeathUpdate();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return this.dataManager.get(ANIMATION_SYNC).currentAnimation!=AnimationType.DEATH ? SoundRegistry.STATIC_SOUND : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    public static class AnimationData {

        @SideOnly(Side.CLIENT)
        private ShapeHolder altRender;
        public AnimationType currentAnimation;
        public long currentAnimationTime;

        private AnimationData() {
            this.currentAnimation = AnimationType.SPAWN;
        }

        public AnimationData(PacketBuffer buf) {
            this.currentAnimation = AnimationType.BY_NAME.get(NetworkUtil.readString(buf));
            this.currentAnimationTime = buf.readLong();
        }

        public AnimationData makeCopy() {
            AnimationData copy = new AnimationData();
            copy.currentAnimation = this.currentAnimation;
            copy.currentAnimationTime = this.currentAnimationTime;
            return copy;
        }

        private NBTTagCompound writeToNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("currentAnimationName",this.currentAnimation.name);
            tag.setLong("currentAnimationTime",this.currentAnimationTime);
            return tag;
        }

        private void readFromNBT(NBTTagCompound tag) {
            this.currentAnimation = AnimationType.BY_NAME.get(tag.getString("currentAnimationName"));
            this.currentAnimationTime = tag.getLong("currentAnimationTime");
        }

        private void tickAnimations(NightTerrorEntity entity) {
            this.currentAnimationTime++;
            if(this.currentAnimationTime>this.currentAnimation.time) {
                if(this.currentAnimation==AnimationType.DEATH) entity.setDead();
                else setAnimation(this.currentAnimation.nextTypeName,0L);
            }
        }

        public void setAnimation(String animationType, long offset) {
            setAnimation(AnimationType.BY_NAME.get(animationType),offset);
        }

        public void setAnimation(AnimationType type, long offset) {
            this.currentAnimation = type;
            this.currentAnimationTime = offset;
        }

        @SideOnly(Side.CLIENT)
        public void applyAltRenderSettings(Consumer<ShapeHolder> settings) {
            if(Objects.isNull(this.altRender))
                this.altRender = new ShapeHolder(Shapes.BOX.makeInstance()).setRelativePosition(Vec3d.ZERO);
            settings.accept(this.altRender);
        }

        @SideOnly(Side.CLIENT)
        public void renderAlt(Vec3d renderAt) {
            if(Objects.isNull(this.altRender))
                this.altRender = new ShapeHolder(Shapes.BOX.makeInstance()).setRelativePosition(Vec3d.ZERO);
            this.altRender.render(renderAt);
        }
    }

    public enum AnimationType {

        DAMAGE("damage",15,"teleport"),
        DEATH("death",123,"idle"),
        IDLE("idle",Long.MAX_VALUE,"idle"),
        SPAWN("spawn",200,"idle"),
        TELEPORT("teleport",100,"idle");

        private static final Map<String,AnimationType> BY_NAME = new HashMap<>();

        static {
            for(AnimationType type : AnimationType.values()) BY_NAME.put(type.name,type);
        }

        private final String name;
        private final long time;
        private final String nextTypeName;

        AnimationType(String name, long time, String next) {
            this.name = name;
            this.time = time;
            this.nextTypeName = next;
        }

        public String getName() {
            return this.name;
        }

        public long getTotalTime() {
            return this.time;
        }
    }
}
