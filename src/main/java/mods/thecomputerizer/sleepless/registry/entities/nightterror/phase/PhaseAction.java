package mods.thecomputerizer.sleepless.registry.entities.nightterror.phase;

import mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender;
import mods.thecomputerizer.sleepless.client.render.geometry.TickableColumn;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomEntity;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import mods.thecomputerizer.sleepless.util.VectorRandomizer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class PhaseAction {

    private final Type type;
    private final int maxTime;
    private int time;
    private long animationOffset;
    private PhaseAction nextAction;
    private boolean invertInvul = false;

    public PhaseAction(NBTTagCompound tag) {
        this.type = Type.BY_NAME.get(tag.getString("actionType"));
        this.maxTime = tag.getInteger("totalActionTime");
        this.time = tag.getInteger("actionTime");
        this.invertInvul = tag.getBoolean("invertInvulnerability");
        if(tag.hasKey("nextAction"))
            this.nextAction = new PhaseAction(tag.getCompoundTag("nextAction"));
    }

    private PhaseAction(Type type, int time) {
        this.type = type;
        this.maxTime = time;
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

    public PhaseAction getTickedAction(NightTerrorEntity entity) {
        boolean isNext = this.time--<=0;
        if(isNext) this.type.onFinish(entity,this.invertInvul);
        else if(this.maxTime>0 && this.time==this.maxTime-1) this.type.onStart(entity,this.animationOffset,this.invertInvul);
        return isNext ? this.nextAction : this;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("actionType",this.type.name);
        tag.setInteger("totalActionTime",this.maxTime);
        tag.setInteger("actionTime",this.time);
        tag.setBoolean("invertInvulnerability",this.invertInvul);
        if(Objects.nonNull(this.nextAction)) tag.setTag("nextAction",this.nextAction.writeToNBT());
        return tag;
    }

    public enum Type {

        DAMAGE("damage",true,1,1,NightTerrorEntity.AnimationType.DAMAGE,
                entity -> {
                    World world = entity.getEntityWorld();
                    if(!world.isRemote) {
                        float healthFactor = 1f-entity.getHealthPercent();
                        int numSpawns = Math.max(1,(int)(healthFactor*5f));
                        spawnPhantoms(world,entity.getPositionVector(),numSpawns,healthFactor,phantom -> {
                            setPhantomAttribute(phantom,SharedMonsterAttributes.ATTACK_DAMAGE,healthFactor,2d);
                            setPhantomAttribute(phantom,SharedMonsterAttributes.MOVEMENT_SPEED,healthFactor,1.2d);
                            phantom.setLifespan(600-(int)(300f*healthFactor));
                            phantom.presetClass(EntityPlayer.class);
                            phantom.markAggressive();
                        });
                    }
                    VectorRandomizer rand = new VectorRandomizer(world.rand,-10d,10d,-10d,10d,20d,10d);
                    entity.setTeleportTarget(rand.rollOffset(entity.getPositionVector()),true);
                },entity -> {}),
        FLOAT("float",false,3,1,null,entity -> {
            Vec3d posVec = entity.getPositionVector();
            int ticks = entity.getHealthPercent()<=0.25f ? 20 : 15;
            if(!entity.world.isRemote) {
                boolean hitPlayer = false;
                for(EntityPlayer p : entity.world.playerEntities) {
                    EntityPlayerMP player = (EntityPlayerMP)p;
                    if(SleepLessConfigHelper.nightTerrorChance(player)>0 && isInXZRange(player.getPositionVector(),posVec,5d)) {
                        player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION,ticks*20));
                        hitPlayer = true;
                    }
                }
                if(hitPlayer) SoundUtil.playRemoteGlobalSound(true,entity.world,SoundRegistry.BELL_SOUND,
                        SoundCategory.HOSTILE,0.75f,1f);
            } else {
                StaticGeometryRender render = new StaticGeometryRender(Minecraft.getMinecraft().getRenderManager(),
                        Minecraft.getMinecraft().player.getPositionVector());
                render.addColumn(new TickableColumn(entity.world.rand,new Vec3d(0d,-200d,0d),
                        1000d,5d,5d).setTime(ticks).init());
                StaticGeometryRender.STATIC_RENDERS.add(render);
            }
        },entity -> {}),
        SPAWN("spawn",true,3,1,null,entity -> {
            World world = entity.getEntityWorld();
            if(!world.isRemote) {
                float healthFactor = 1f-entity.getHealthPercent();
                int numSpawns = Math.max(2,(int)(healthFactor*10f));
                spawnPhantoms(world,entity.getPositionVector(),numSpawns,healthFactor,phantom -> {
                    setPhantomAttribute(phantom,SharedMonsterAttributes.ATTACK_DAMAGE,healthFactor,2d);
                    setPhantomAttribute(phantom,SharedMonsterAttributes.MOVEMENT_SPEED,healthFactor,1.2d);
                    phantom.setLifespan(600-(int)(300f*healthFactor));
                    phantom.presetClass(EntityPlayer.class);
                    phantom.markAggressive();
                });
            }
        },entity -> {}),
        TELEPORT("teleport",true,0,1,NightTerrorEntity.AnimationType.TELEPORT,
                entity -> {
                    entity.addPotionEffect(new PotionEffect(PotionRegistry.PHASED,50));
                    entity.setMoveTarget(1d);
                    playSound(entity,SoundRegistry.BOOSTED_TP_SOUND,1f,0.5f);
                },
                entity -> playSound(entity,SoundRegistry.BOOSTED_TP_REVERSE_SOUND,1f,0.5f)),
        TRANSITION("transition",true,0,1,
                NightTerrorEntity.AnimationType.SPAWN,entity -> {}, entity -> {}),
        WAIT("wait",false,3,1,null,
                entity -> {
                    EntityLivingBase target = entity.getAttackTarget();
                    if(Objects.nonNull(target)) entity.setTeleportTarget(target.posX+1,target.posY,target.posZ,false);
                },entity -> {});

        private static final Map<String,Type> BY_NAME = new HashMap<>();

        static {
            for(Type type : Type.values()) BY_NAME.put(type.name,type);
        }

        private static boolean isInXZRange(Vec3d posVec, Vec3d centerVec, double range) {
            return Math.abs(centerVec.x-posVec.x)<=range && Math.abs(centerVec.z-posVec.z)<=range;
        }

        private static void setPhantomAttribute(PhantomEntity phantom, IAttribute attribute, float factor, double maxFactor) {
            double min = phantom.getEntityAttribute(attribute).getBaseValue();
            double max = min*maxFactor;
            phantom.getEntityAttribute(attribute).setBaseValue(min+(factor*(max-min)));
        }

        private static void spawnPhantoms(World world, Vec3d spawnVec, int numSpawns, double randFactor,
                                          Consumer<PhantomEntity> phantomSettings) {
            if(numSpawns<=1) PhantomEntity.spawnPhantom(world,spawnVec.x,spawnVec.y,spawnVec.z,phantomSettings);
            else {
                double randXZ = 4d+(4d*randFactor);
                VectorRandomizer rand = new VectorRandomizer(world.rand,-randXZ,1d,-randXZ,randXZ,3d,randXZ);
                for(int i=0; i<numSpawns; i++) {
                    PhantomEntity.spawnPhantom(world,spawnVec.x,spawnVec.y,spawnVec.z,phantomSettings);
                    Vec3d randSpawn = rand.rollOffset(spawnVec);
                    spawnVec.add(0d,randSpawn.y-spawnVec.y,0d);
                }
            }
        }

        private static void playSound(EntityLiving entity, SoundEvent sound, float volume, float pitch) {
            SoundUtil.playRemoteEntitySound(entity,sound,false,volume,pitch);
        }

        private final String name;
        private final boolean isInvulnerable;
        private final int startRenderMode;
        private final int finishRenderMode;
        private final @Nullable NightTerrorEntity.AnimationType animationType;
        private final Consumer<NightTerrorEntity> extraStartFunc;
        private final Consumer<NightTerrorEntity> extraFinishFunc;
        Type(String name, boolean isInvulnerable, int startRenderMode, int finishRenderMode,
             @Nullable NightTerrorEntity.AnimationType animationType, Consumer<NightTerrorEntity> extraStartFunc,
             Consumer<NightTerrorEntity> extraFinishFunc) {
            this.name = name;
            this.isInvulnerable = isInvulnerable;
            this.startRenderMode = startRenderMode;
            this.finishRenderMode = finishRenderMode;
            this.animationType = animationType;
            this.extraStartFunc = extraStartFunc;
            this.extraFinishFunc = extraFinishFunc;
        }

        public PhaseAction create(int time) {
            return new PhaseAction(this,time);
        }

        public void onStart(NightTerrorEntity entity, long animationOffset, boolean invertInvul) {
            boolean invul = this.isInvulnerable;
            if(invertInvul) invul = !invul;
            entity.setEntityInvulnerable(invul);
            entity.setGlowing(invul);
            if(Objects.nonNull(this.animationType))
                entity.setAnimation(this.animationType,animationOffset);
            this.extraStartFunc.accept(entity);
            if(entity.world.isRemote)
                entity.renderMode = this.startRenderMode==3 ? (invul ? 1 : 2) : this.startRenderMode;
        }

        public void onFinish(NightTerrorEntity entity, boolean invertInvul) {
            boolean invul = this.isInvulnerable;
            if(invertInvul) invul = !invul;
            this.extraFinishFunc.accept(entity);
            if(entity.world.isRemote)
                entity.renderMode = this.finishRenderMode==3 ? (invul ? 1 : 2) : this.finishRenderMode;
        }
    }
}
