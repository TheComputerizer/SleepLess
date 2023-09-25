package mods.thecomputerizer.sleepless.registry.entities;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PhantomEntity extends EntityLiving {

    private static final Class<?>[] VALID_SHADOWS = new Class<?>[]{EntityZombie.class,EntitySkeleton.class,
            EntityCreeper.class};
    protected EntityEntry shadowEntry;

    public PhantomEntity(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.setSize(1f, 1.875f);
    }

    @SuppressWarnings("unchecked")
    protected void setRandomShadow() {
        Class<?> shadowClass = VALID_SHADOWS[this.world.rand.nextInt(VALID_SHADOWS.length)];
        if(Entity.class.isAssignableFrom(shadowClass))
            this.shadowEntry = EntityRegistry.getEntry((Class<? extends Entity>)shadowClass);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        setRandomShadow();
    }

    public boolean isInitialized() {
        return Objects.nonNull(this.shadowEntry);
    }

    @Override
    protected void damageEntity(@Nonnull DamageSource source, float amount) {
        super.damageEntity(source,amount);
        setRandomShadow();
    }

    @SideOnly(Side.CLIENT)
    public Render<?> getShadowRenderer(@Nonnull RenderManager manager) {
        if(Objects.isNull(this.shadowEntry)) return null;
        return manager.entityRenderMap.getOrDefault(this.shadowEntry.getEntityClass(),null);
    }
}
