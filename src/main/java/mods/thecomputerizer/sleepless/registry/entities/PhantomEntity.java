package mods.thecomputerizer.sleepless.registry.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PhantomEntity extends EntityLiving {

    private static final Class<?>[] VALID_SHADOWS = new Class<?>[]{EntityPlayer.class};
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
        this.shadowEntityClass = Entity.class.isAssignableFrom(potentialClass) ?
                (Class<? extends Entity>)potentialClass : null;
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

    @SideOnly(Side.CLIENT)
    public Render<?> getShadowRenderer(@Nonnull RenderManager manager) {
        if(Objects.isNull(this.shadowEntityClass)) return null;
        if(EntityPlayer.class.isAssignableFrom(this.shadowEntityClass)) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            return Objects.nonNull(player) ? manager.getEntityRenderObject(Minecraft.getMinecraft().player) : null;
        }
        return manager.entityRenderMap.getOrDefault(this.shadowEntityClass,null);
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
}
