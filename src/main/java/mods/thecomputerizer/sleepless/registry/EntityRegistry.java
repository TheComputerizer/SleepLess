package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.entities.PhantomEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SameParameterValue", "unchecked", "unused"})
public final class EntityRegistry {
    private static final List<EntityEntry> ALL_ENTRIES = new ArrayList<>();
    public static final EntityEntry PHANTOM_ENTITY = makeEntry("phantom_entity", PhantomEntity.class,0,0);
    private static int entityIdCounter = 0;

    private static <E extends Entity> EntityEntry makeEntry(final String name, final Class<E> entityClass,
                                                            int spawnEggColor1, int spawnEggColor2) {
        final EntityEntryBuilder<E> builder = EntityEntryBuilder.create();
        EntityEntryBuilder<E>.BuiltEntityEntry entry = (EntityEntryBuilder<E>.BuiltEntityEntry)builder.entity(entityClass)
                .tracker(100,1,true).egg(spawnEggColor1,spawnEggColor2)
                .name(name).id(Constants.res(name),entityIdCounter++).build();
        entry.addedToRegistry();
        ALL_ENTRIES.add(entry);
        return entry;
    }

    public static EntityEntry[] getEntityEntries() {
        return ALL_ENTRIES.toArray(new EntityEntry[0]);
    }
}
