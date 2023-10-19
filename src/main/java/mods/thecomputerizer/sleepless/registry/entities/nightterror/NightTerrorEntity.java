package mods.thecomputerizer.sleepless.registry.entities.nightterror;

import mcp.MethodsReturnNonnullByDefault;
import mods.thecomputerizer.sleepless.client.render.geometry.ShapeHolder;
import mods.thecomputerizer.sleepless.client.render.geometry.Shapes;
import mods.thecomputerizer.sleepless.config.SleepLessConfig;
import mods.thecomputerizer.sleepless.registry.DataSerializerRegistry;
import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import mods.thecomputerizer.sleepless.registry.entities.ai.EntityWatchClosestWithSleepDebt;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import mods.thecomputerizer.theimpossiblelibrary.util.NetworkUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
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
public class NightTerrorEntity extends EntityLiving {

    private static final DataParameter<AnimationData> ANIMATION_SYNC = EntityDataManager.createKey(NightTerrorEntity.class,
            (DataSerializer<AnimationData>)DataSerializerRegistry.ANIMATION_SERIALIZER.getSerializer());

    @SideOnly(Side.CLIENT)
    public int renderMode = 0;
    private long ticksAlive;

    public NightTerrorEntity(World world) {
        super(world);
        this.ignoreFrustumCheck = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ANIMATION_SYNC,new AnimationData());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(6,new EntityWatchClosestWithSleepDebt(this,64f,SleepLessConfig.NIGHT_TERROR.minSleepDebt,1f));
    }

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

    public long getTicksAlive() {
        return this.ticksAlive;
    }

    public AnimationData getAnimationData() {
        return this.dataManager.get(ANIMATION_SYNC);
    }

    public void setAnimation(AnimationType type) {
        getAnimationData().setAnimation(type,this);
        this.dataManager.setDirty(ANIMATION_SYNC);
    }

    @Override
    public void onLivingUpdate() {
        if(!this.dead) {
            this.ticksAlive++;
            this.dataManager.get(ANIMATION_SYNC).tickAnimations(this);
        }
        super.onLivingUpdate();
    }

    @Override
    protected void damageEntity(DamageSource source, float damageAmount) {
        if(this.dataManager.get(ANIMATION_SYNC).currentAnimation==AnimationType.DEATH) return;
        float health = this.getHealth();
        super.damageEntity(source,damageAmount);
        if(this.getHealth()<health) {
            this.setAnimation(this.getHealth() <= 0 ? AnimationType.DEATH : AnimationType.DAMAGE);
        }
        if(this.getHealth()<=0f) this.setHealth(0.01f);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setLong("nightTerrorTicksAlive",this.ticksAlive);
        tag.setTag("nightTerrorAnimationData",this.dataManager.get(ANIMATION_SYNC).writeToNBT());
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
            if(this.currentAnimationTime>this.currentAnimation.time)
                setAnimation(this.currentAnimation.nextTypeName,entity);
        }

        public void setAnimation(String animationType, NightTerrorEntity entity) {
            setAnimation(AnimationType.BY_NAME.get(animationType),entity);
        }

        public void setAnimation(AnimationType type, NightTerrorEntity entity) {
            if(type!=this.currentAnimation)
                this.currentAnimation.onFinish.accept(entity);
            this.currentAnimation = type;
            this.currentAnimation.onStart.accept(entity);
            this.currentAnimationTime = 0L;
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

        DAMAGE("damage",15,"teleport",entity -> entity.setEntityInvulnerable(true),entity -> {}),
        DEATH("death",123,"idle",entity -> entity.setEntityInvulnerable(true),entity -> entity.setHealth(0f)),
        IDLE("idle",Long.MAX_VALUE,"idle",entity -> {},entity -> {}),
        SPAWN("spawn",200,"idle",entity -> entity.setEntityInvulnerable(true),
                entity -> entity.setEntityInvulnerable(false)),
        TELEPORT("teleport",100,"idle",
                entity -> SoundUtil.playRemoteEntitySound(entity,SoundRegistry.BOOSTED_TP_SOUND,false,entity.getSoundVolume(),0.5f),
                entity -> SoundUtil.playRemoteEntitySound(entity,SoundRegistry.BOOSTED_TP_REVERSE_SOUND,false,entity.getSoundVolume(),0.5f));

        private static final Map<String,AnimationType> BY_NAME = new HashMap<>();
        private final String name;
        private final long time;
        private final String nextTypeName;
        private final Consumer<NightTerrorEntity> onStart;
        private final Consumer<NightTerrorEntity> onFinish;

        AnimationType(String name, long time, String next, Consumer<NightTerrorEntity> onStart,
                      Consumer<NightTerrorEntity> onFinish) {
            this.name = name;
            this.time = time;
            this.nextTypeName = next;
            this.onStart = onStart;
            this.onFinish = onFinish;
        }

        public String getName() {
            return this.name;
        }

        public long getTotalTime() {
            return this.time;
        }

        static {
            for(AnimationType type : AnimationType.values()) BY_NAME.put(type.name,type);
        }
    }
}
