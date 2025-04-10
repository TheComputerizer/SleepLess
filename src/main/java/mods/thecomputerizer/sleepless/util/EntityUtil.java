package mods.thecomputerizer.sleepless.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.registry.entities.ai.EntityWatchClosestWithSleepDebt;
import mods.thecomputerizer.sleepless.registry.entities.ai.EntityWatchClosestWithSleepDebt2;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.LOGGER;

@SuppressWarnings("unused")
public class EntityUtil {

    private static final Map<Class<? extends EntityAIBase>,Class<? extends EntityAIBase>> MAPPED_PHANTOM_AI = new HashMap<>();

    private static void mapPhantomAI() {
        MAPPED_PHANTOM_AI.put(EntityAIWatchClosest.class,EntityWatchClosestWithSleepDebt.class);
        MAPPED_PHANTOM_AI.put(EntityAIWatchClosest2.class,EntityWatchClosestWithSleepDebt2.class);
    }

    /**
     * Arrays should be slightly more efficient than collections
     * Iterators should be slightly more efficient than Collection#removeIf
     */
    public static void makePhantomAITasks(EntityAITasks tasks, float minSleepDebt) {
        Set<Entry<Integer,EntityAIBase>> newEntries = new HashSet<>();
        int i = 0;
        Iterator<EntityAITaskEntry> entryItr = tasks.taskEntries.iterator();
        while(entryItr.hasNext()) {
            EntityAITaskEntry entry = entryItr.next();
            Entry<Integer,EntityAIBase> newEntry = makePhantomAITask(entry.priority,entry.action,minSleepDebt);
            if(Objects.isNull(newEntry)) entryItr.remove();
            else newEntries.add(newEntry);
            i++;
        }
        for(Entry<Integer,EntityAIBase> newEntry : newEntries)
            if(Objects.nonNull(newEntry))
                tasks.addTask(newEntry.getKey(),newEntry.getValue());
    }

    public static Entry<Integer,EntityAIBase> makePhantomAITask(int priority, EntityAIBase task, float minSleepDebt) {
        Class<? extends EntityAIBase> mappedClass = MAPPED_PHANTOM_AI.get(task.getClass());
        if(Objects.isNull(mappedClass)) return null;
        EntityAIBase mappedInstance = instantiatePhantomTask(task,minSleepDebt,mappedClass);
        return Objects.nonNull(mappedInstance) ? new SimpleImmutableEntry<>(priority,mappedInstance) : null;
    }

    private static EntityAIBase instantiatePhantomTask(EntityAIBase originalTask, float minSleepDebt,
            Class<? extends EntityAIBase> mappedClass) {
        Class<? extends EntityAIBase> originalClass = originalTask.getClass();
        try {
            Constructor<? extends EntityAIBase> constructor = mappedClass.getDeclaredConstructor(originalClass,Float.class);
            return constructor.newInstance(originalTask,minSleepDebt);
        } catch(NoSuchMethodException ex) {
            LOGGER.error("Unable to find valid contructor with inputs ({}[{}], {}[{}]) for mapped"+
                                      " phantom task of class {}",originalClass.getName()+" instance",originalTask,
                                      "float minSleepDebt",minSleepDebt,mappedClass,ex);
        } catch(InvocationTargetException ex) {
            LOGGER.error("Unable to invoke contructor with inputs ({}[{}], {}[{}]) for mapped phantom"+
                         " task of class {}",originalClass.getName()+" instance",originalTask,
                         "float minSleepDebt",minSleepDebt,mappedClass,ex);
        } catch(InstantiationException ex) {
            LOGGER.error("Unable to instantiate phantom task from contructor with inputs ({}[{}], "+
                         "{}[{}]) for mapped phantom task of class {}",originalClass.getName()+" instance",
                         originalTask,"float minSleepDebt",minSleepDebt,mappedClass,ex);
        } catch(IllegalAccessException ex) {
            LOGGER.error("Tried to illegally access contructor with inputs ({}[{}], {}[{}]) for"+
                         " mapped phantom task of class {}",originalClass.getName()+" instance", originalTask,
                         "float minSleepDebt",minSleepDebt,mappedClass,ex);
        }
        return null;
    }

    public static EntityPlayer getClosestPlayerWithSleepDebt(float minSleepDebt, World world, Vec3d posVec,
            double distance) {
        return getClosestPlayerWithSleepDebt(minSleepDebt,world,posVec,distance,null);
    }

    public static EntityPlayer getClosestPlayerWithSleepDebt(float minSleepDebt, World world, double x, double y,
            double z, double distance) {
        return getClosestPlayerWithSleepDebt(minSleepDebt,world,x,y,z,distance,null);
    }

    @SuppressWarnings("Guava")
    public static EntityPlayer getClosestPlayerWithSleepDebt(float minSleepDebt, World world, Vec3d posVec,
            double distance, @Nullable Predicate<Entity> predicate) {
        return getClosestPlayerWithSleepDebt(minSleepDebt,world,posVec.x,posVec.y,posVec.z,distance,predicate);
    }

    @SuppressWarnings("Guava")
    public static EntityPlayer getClosestPlayerWithSleepDebt(float minSleepDebt, World world, double x, double y,
            double z, double distance, @Nullable Predicate<Entity> predicate) {
        Predicate<Entity> sleepDebtPredicate = getSleepDebtPredicate(minSleepDebt);
        return world.getClosestPlayer(x,y,z,distance,Objects.nonNull(predicate) ?
                Predicates.and(predicate,sleepDebtPredicate) : sleepDebtPredicate);
    }

    @SuppressWarnings("Guava")
    public static Predicate<Entity> getSleepDebtPredicate(float minSleepDebt) {
        return input -> minSleepDebt<0 ||CapabilityHandler.getSleepDebt(input)>=minSleepDebt;
    }

    public static double getTotalJumpHeight(double motion, double gravityFactor) {
        double height = 0;
        if(motion>0d) {
            while(motion>gravityFactor) {
                height+=motion;
                motion-=gravityFactor;
            }
            if(motion>0d) height+=motion;
        }
        return height;
    }

    public static List<AxisAlignedBB> getSpecificBlockCollisions(World world, AxisAlignedBB aabb, Block ... badBlocks) {
        List<AxisAlignedBB> ret = new ArrayList<>();
        int minX = MathHelper.floor(aabb.minX)-1;
        int maxX = MathHelper.ceil(aabb.maxX)+1;
        int minY = MathHelper.floor(aabb.minY)-1;
        int maxY = MathHelper.ceil(aabb.maxY)+1;
        int minZ = MathHelper.floor(aabb.minZ)-1;
        int MaxZ = MathHelper.ceil(aabb.maxZ)+1;
        WorldBorder worldborder = world.getWorldBorder();
        MutableBlockPos mutablePos = new MutableBlockPos();
        for(int x=minX; x<maxX; x++) {
            for(int z=minZ; z<MaxZ; z++) {
                boolean isBorderX = x==minX || x==maxX-1;
                boolean isBorderZ = z==minZ || z==MaxZ-1;
                if((!isBorderX || !isBorderZ) && world.isBlockLoaded(mutablePos.setPos(x,64,z))) {
                    for(int y=minY; y<maxY; y++) {
                        if(!isBorderX && !isBorderZ || y!=maxY-1) {
                            mutablePos.setPos(x,y,z);
                            if(!worldborder.contains(mutablePos)) {
                                addBBToList(world,mutablePos,aabb,ret);
                                continue;
                            }
                            Block block = world.getBlockState(mutablePos).getBlock();
                            for(Block badBlock : badBlocks) {
                                if(block==badBlock) {
                                    addBBToList(world,mutablePos,aabb,ret);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private static void addBBToList(World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> list) {
        world.getBlockState(pos).addCollisionBoxToList(world,pos,aabb,list,null,false);
    }
}