package mods.thecomputerizer.sleepless.registry.entities;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PhantomEntity extends EntityLiving {

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
}
