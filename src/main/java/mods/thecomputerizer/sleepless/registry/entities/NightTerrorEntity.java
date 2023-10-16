package mods.thecomputerizer.sleepless.registry.entities;

import mods.thecomputerizer.sleepless.client.render.geometry.ShapeHolder;
import mods.thecomputerizer.sleepless.client.render.geometry.Shapes;
import mods.thecomputerizer.sleepless.config.SleepLessConfig;
import mods.thecomputerizer.sleepless.network.PacketSendNightTerrorAnimtion;
import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import mods.thecomputerizer.sleepless.world.ai.EntityWatchClosestWithSleepDebt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class NightTerrorEntity extends EntityLiving {

    @SideOnly(Side.CLIENT)
    public int renderMode = 0;
    private final AnimationData animationData;
    private long ticksAlive;

    public NightTerrorEntity(World world) {
        super(world);
        this.ignoreFrustumCheck = true;
        this.animationData = new AnimationData();
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(6,new EntityWatchClosestWithSleepDebt(this,64f,SleepLessConfig.NIGHT_TERROR.minSleepDebt));
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
        return this.animationData;
    }

    @Override
    public void onLivingUpdate() {
        if(!this.dead) {
            if(this.ticksAlive==0) this.animationData.setAnimation(AnimationType.SPAWN);
            this.ticksAlive++;
            this.animationData.tickAnimations(FMLCommonHandler.instance().getEffectiveSide().isClient());
        }
        super.onLivingUpdate();
    }

    @Override
    protected void damageEntity(DamageSource source, float damageAmount) {
        float health = this.getHealth();
        super.damageEntity(source,damageAmount);
        if(this.getHealth()<health)
            this.animationData.setAnimation(this.getHealth()<=0 ? AnimationType.DEATH : AnimationType.DAMAGE);
        if(this.getHealth()<=0f) {
            this.setHealth(0.01f);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setLong("nightTerrorTicksAlive",this.ticksAlive);
        tag.setTag("nightTerrorAnimationData",this.animationData.writeToNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.ticksAlive = tag.getLong("nightTerrorTicksAlive");
        this.animationData.readFromNBT(tag.getCompoundTag("nightTerrorAnimationData"));
        if(this.animationData.currentAnimation==AnimationType.SPAWN && this.ticksAlive>=200)
            this.animationData.setAnimation(AnimationType.IDLE);
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
        return this.animationData.currentAnimation!=AnimationType.DEATH ? SoundRegistry.STATIC_SOUND : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    public class AnimationData {

        @SideOnly(Side.CLIENT)
        private ShapeHolder altRender;
        public AnimationType currentAnimation;
        public long currentAnimationTime;

        private AnimationData() {
            this.currentAnimation = AnimationType.SPAWN;
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
            checkSync();
        }

        private void tickAnimations(boolean isClient) {
            this.currentAnimationTime++;
            if(this.currentAnimationTime>this.currentAnimation.time)
                setAnimation(this.currentAnimation.nextTypeName);
        }

        public void setAnimation(String animationType) {
            setAnimation(AnimationType.BY_NAME.get(animationType));
        }

        public void setAnimation(AnimationType type) {
            if(type!=this.currentAnimation)
                this.currentAnimation.onFinish.accept(NightTerrorEntity.this);
            this.currentAnimation = type;
            this.currentAnimation.onStart.accept(NightTerrorEntity.this);
            this.currentAnimationTime = 0L;
            checkSync();
        }

        private void checkSync() {
            if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
                NightTerrorEntity entity = NightTerrorEntity.this;
                PacketSendNightTerrorAnimtion packet = new PacketSendNightTerrorAnimtion(NightTerrorEntity.this.getEntityId(),this.currentAnimation.name);
                for(EntityPlayer player : entity.getEntityWorld().playerEntities)
                    packet.addPlayers((EntityPlayerMP)player);
                packet.send();
            }
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

        DAMAGE("damage",false,15,"teleport",entity -> entity.setEntityInvulnerable(true),entity -> {}),
        DEATH("death",true,123,"idle",entity -> {
            entity.setEntityInvulnerable(true);
            entity.playSound(SoundRegistry.BELL_REVERSE_SOUND,entity.getSoundVolume(),entity.getSoundPitch());
        },entity -> entity.setHealth(0f)),
        IDLE("idle",false,Long.MAX_VALUE,"idle",entity -> {},entity -> {}),
        SPAWN("spawn",true,200,"idle",entity -> entity.setEntityInvulnerable(true),
                entity -> entity.setEntityInvulnerable(false)),
        TELEPORT("teleport",true,100,"idle",entity -> {
            if(entity.world.isRemote) Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(
                    SoundRegistry.BOOSTED_TP_SOUND,SoundCategory.HOSTILE,1f,0.5f,
                    (float)entity.posX,(float)entity.posY,(float)entity.posZ));
        },entity -> {
            if(entity.world.isRemote) Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(
                    SoundRegistry.BOOSTED_TP_REVERSE_SOUND,SoundCategory.HOSTILE,1f,0.5f,
                    (float)entity.posX,(float)entity.posY,(float)entity.posZ));
            entity.setEntityInvulnerable(false);
        });

        private static final Map<String,AnimationType> BY_NAME = new HashMap<>();
        private final String name;
        private final boolean instant;
        private final long time;
        private final String nextTypeName;
        private final Consumer<NightTerrorEntity> onStart;
        private final Consumer<NightTerrorEntity> onFinish;

        AnimationType(String name, boolean instant, long time, String next, Consumer<NightTerrorEntity> onStart,
                      Consumer<NightTerrorEntity> onFinish) {
            this.name = name;
            this.instant = instant;
            this.time = time;
            this.nextTypeName = next;
            this.onStart = onStart;
            this.onFinish = onFinish;
        }

        public long getTotalTime() {
            return this.time;
        }

        static {
            for(AnimationType type : AnimationType.values()) BY_NAME.put(type.name,type);
        }
    }
}
