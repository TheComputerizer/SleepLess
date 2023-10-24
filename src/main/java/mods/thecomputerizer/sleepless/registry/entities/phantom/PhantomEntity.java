package mods.thecomputerizer.sleepless.registry.entities.phantom;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.registry.DataSerializerRegistry;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.ai.EntityWatchClosestWithSleepDebt;
import mods.thecomputerizer.sleepless.registry.entities.ai.PhantomNearestAttackableTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
public class PhantomEntity extends EntityMob {

    private static final DataParameter<Class<?>> CLASS_TYPE_SYNC = EntityDataManager.createKey(PhantomEntity.class,
            (DataSerializer<Class<?>>)DataSerializerRegistry.CLASS_SERIALIZER.getSerializer());

    public static void spawnPhantom(World world, Consumer<PhantomEntity> spawnSettings) {
        PhantomEntity phantom = new PhantomEntity(world);
        spawnSettings.accept(phantom);
        world.spawnEntity(phantom);
    }

    private boolean presetClass = false;
    private boolean isAggressive = false;
    private int lifespan = -1;
    private float minPlayerDistance = 0f;

    public PhantomEntity(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.setSize(1f,1.875f);
        this.addPotionEffect(new PotionEffect(PotionRegistry.PHASED,Integer.MAX_VALUE));
        this.ignoreFrustumCheck = true;
    }

    public void presetClass(@Nullable Class<?> potentialClass) {
        if(tryAssignShadowClass(potentialClass)) this.presetClass = true;
    }

    public void markAggressive() {
        this.isAggressive = true;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public void setDespawnDistance(float dist) {
        this.minPlayerDistance = dist;
    }

    @Override
    public @Nullable IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
        data = super.onInitialSpawn(difficulty,data);
        if(!this.presetClass) {
            List<Biome.SpawnListEntry> spawnEntryHooks = this.world.getBiome(this.getPosition()).getSpawnableList(EnumCreatureType.MONSTER);
            if(!spawnEntryHooks.isEmpty()) {
                Biome.SpawnListEntry entry = WeightedRandom.getRandomItem(this.rand,spawnEntryHooks);
                if(!PhantomEntity.class.isAssignableFrom(entry.entityClass)) tryAssignShadowClass(entry.entityClass);
            }
        }
        if(this.lifespan<0) this.lifespan = 1200;
        return data;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64d);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25d);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2d);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CLASS_TYPE_SYNC,EntityZombie.class);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(3,new EntityAIAttackMelee(this,1d,false));
        this.tasks.addTask(6,new EntityWatchClosestWithSleepDebt(this,64f,5f,1f));
        this.targetTasks.addTask(1,new PhantomNearestAttackableTarget<>(this,EntityPlayer.class,1, false,
                false,7f,this::isAggressive));
    }

    /*
    @Override
    protected @Nonnull PathNavigate createNavigator(World world) {
        return new PhantomPathNavigateGround(this,world);
    }
     */

    @Override
    public float getWaterSlowDown() {
        return 1f;
    }

    @Override
    public int getMaxFallHeight() {
        return 1000;
    }

    public boolean isAggressive() {
        return this.isAggressive;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if(this.isDead) return;
        if(this.lifespan>0) this.lifespan--;
        if(this.lifespan==0) this.setDead();
        else if(this.lifespan%5==0 && this.minPlayerDistance>0f && !this.isAggressive) {
            for(EntityPlayer player : this.world.playerEntities) {
                if(player.getPosition().getDistance((int)this.posX,(int)this.posY,(int)this.posZ)<=this.minPlayerDistance &&
                        CapabilityHandler.getPhantomFactor(player)>0f) {
                    this.setDead();
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean tryAssignShadowClass(@Nullable Class<?> potentialClass) {
        Class<? extends Entity> nextClass = Objects.nonNull(potentialClass) && Entity.class.isAssignableFrom(potentialClass) ?
                (Class<? extends Entity>)potentialClass : null;
        if(Objects.nonNull(nextClass) && !PhantomEntity.class.isAssignableFrom(potentialClass)) {
            this.dataManager.set(CLASS_TYPE_SYNC,nextClass);
            this.dataManager.setDirty(CLASS_TYPE_SYNC);
            return true;
        }
        return false;
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        NBTTagCompound shadowTag = new NBTTagCompound();
        shadowTag.setString("EntityClassName",this.dataManager.get(CLASS_TYPE_SYNC).getName());
        shadowTag.setBoolean("IsPhantomAggressive",this.isAggressive);
        shadowTag.setInteger("PhantomLifeSpawn",this.lifespan);
        shadowTag.setFloat("DespawnDistance",this.minPlayerDistance);
        tag.setTag("SleepLessShadowData",shadowTag);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        NBTTagCompound shadowTag = tag.getCompoundTag("SleepLessShadowData");
        try {
            tryAssignShadowClass(Class.forName(shadowTag.getString("EntityClassName")));
        } catch (ClassNotFoundException ignored) {}
        this.isAggressive = shadowTag.getBoolean("IsPhantomAggressive");
        this.lifespan = shadowTag.getInteger("PhantomLifeSpawn");
        this.minPlayerDistance = shadowTag.getFloat("DespawnDistance");
    }

    @Override
    public float getBrightness() {
        if(!this.world.isRemote) return super.getBrightness();
        return super.getBrightness()*ClientEffects.PHANTOM_VISIBILITY;
    }

    @SideOnly(Side.CLIENT)
    public Class<? extends Entity> getShadowEntityClass() {
        return (Class<? extends Entity>)this.dataManager.get(CLASS_TYPE_SYNC);
    }

    @SideOnly(Side.CLIENT)
    public Render<?> getShadowRender(@Nonnull RenderManager manager) {
        Class<? extends Entity> shadowClass = getShadowEntityClass();
        if(EntityPlayer.class.isAssignableFrom(shadowClass)) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            return Objects.nonNull(player) ? manager.getEntityRenderObject(Minecraft.getMinecraft().player) : null;
        }
        return getNonPlayerShadowRender(manager,shadowClass);
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    private Render<?> getNonPlayerShadowRender(@Nonnull RenderManager manager, Class<? extends Entity> entityClass) {
        Render<?> render = manager.entityRenderMap.get(entityClass);
        if(Objects.isNull(render) && entityClass!=Entity.class)
            render = getNonPlayerShadowRender(manager,(Class<? extends Entity>)entityClass.getSuperclass());
        return render;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }
}
