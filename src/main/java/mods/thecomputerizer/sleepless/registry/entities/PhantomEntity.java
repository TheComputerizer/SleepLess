package mods.thecomputerizer.sleepless.registry.entities;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.ai.EntityWatchClosestWithSleepDebt;
import mods.thecomputerizer.sleepless.registry.entities.ai.PhantomNearestAttackableTarget;
import mods.thecomputerizer.sleepless.registry.entities.pathfinding.PhantomPathNavigateGround;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class PhantomEntity extends EntityMob {

    private static final Class<?>[] VALID_SHADOWS = new Class<?>[]{EntityZombie.class,EntitySkeleton.class,
            EntityEnderman.class,EntityPlayer.class};

    protected Class<? extends Entity> shadowEntityClass;

    /**
     * Stored class name for caching purposes
     */
    private String shadowEntityClassName;

    public PhantomEntity(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.setSize(1f, 1.875f);
        this.addPotionEffect(new PotionEffect(PotionRegistry.PHASED,Integer.MAX_VALUE));
        this.ignoreFrustumCheck = true;
        tryAssignShadowClass(VALID_SHADOWS[0]);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64d);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15d);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2d);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10d);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(3,new EntityAIAttackMelee(this,1d,false));
        this.tasks.addTask(6,new EntityWatchClosestWithSleepDebt(this,64f,5f,1f));
        this.targetTasks.addTask(1, new PhantomNearestAttackableTarget<>(this,EntityPlayer.class,1, false,false,7f));
    }

    @Override
    protected @Nonnull PathNavigate createNavigator(World world) {
        return new PhantomPathNavigateGround(this,world);
    }

    @SuppressWarnings("unchecked")
    private void tryAssignShadowClass(Class<?> potentialClass) {
        Class<? extends Entity> nextClass = Entity.class.isAssignableFrom(potentialClass) ? (Class<? extends Entity>)potentialClass : null;
        boolean isDifferent = this.shadowEntityClass!=nextClass;
        this.shadowEntityClass = nextClass;
        if(isDifferent)
            this.shadowEntityClassName = Objects.nonNull(this.shadowEntityClass) ? this.shadowEntityClass.getName() : null;
    }

    protected void setRandomShadow() {
        tryAssignShadowClass(VALID_SHADOWS[this.world.rand.nextInt(VALID_SHADOWS.length)]);
    }

    public boolean isInitialized() {
        return Objects.nonNull(this.shadowEntityClass);
    }

    @Override
    protected void damageEntity(@Nonnull DamageSource source, float amount) {
        super.damageEntity(source,amount);
        setRandomShadow();
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        NBTTagCompound shadowTag = new NBTTagCompound();
        if(Objects.nonNull(this.shadowEntityClassName))
            shadowTag.setString("EntityClassName",this.shadowEntityClassName);
        tag.setTag("SleepLessShadowData",shadowTag);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if(Objects.isNull(this.shadowEntityClassName)) {
            readEntityClass(tag.getCompoundTag("SleepLessShadowData").getString("EntityClassName"));
            if(Objects.isNull(this.shadowEntityClassName)) setRandomShadow();
        }
    }

    private void readEntityClass(String className) {
        if(!className.isEmpty()) {
            try {
                tryAssignShadowClass(Class.forName(className));
            } catch (ClassNotFoundException ignored) {}
        }
    }

    @Override
    public float getBrightness() {
        if(!this.world.isRemote) return super.getBrightness();
        return super.getBrightness()*ClientEffects.PHANTOM_VISIBILITY;
    }

    @SideOnly(Side.CLIENT)
    public Class<? extends Entity> getShadowEntityClass() {
        return this.shadowEntityClass;
    }

    @SideOnly(Side.CLIENT)
    public Render<?> getShadowRender(@Nonnull RenderManager manager) {
        if(Objects.isNull(this.shadowEntityClass)) return null;
        if(EntityPlayer.class.isAssignableFrom(this.shadowEntityClass)) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            return Objects.nonNull(player) ? manager.getEntityRenderObject(Minecraft.getMinecraft().player) : null;
        }
        return getNonPlayerShadowRender(manager,this.shadowEntityClass);
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    private Render<?> getNonPlayerShadowRender(@Nonnull RenderManager manager, Class<? extends Entity> entityClass) {
        Render<?> render = manager.entityRenderMap.get(this.shadowEntityClass);
        if(Objects.isNull(render) && this.shadowEntityClass!=Entity.class)
            render = getNonPlayerShadowRender(manager,(Class<? extends Entity>)entityClass.getSuperclass());
        return render;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }
}
