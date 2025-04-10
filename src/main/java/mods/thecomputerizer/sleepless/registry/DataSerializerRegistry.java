package mods.thecomputerizer.sleepless.registry;

import mcp.MethodsReturnNonnullByDefault;
import mods.thecomputerizer.sleepless.core.SleepLessRef;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity.AnimationData;
import mods.thecomputerizer.theimpossiblelibrary.api.network.NetworkHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.LOGGER;

@MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
public class DataSerializerRegistry {

    private static final List<DataSerializerEntry> ALL_SERIALIZERS = new ArrayList<>();
    public static final DataSerializerEntry ANIMATION_SERIALIZER = makeEntry(new DataSerializer<AnimationData>() {
        @Override public void write(PacketBuffer buf, AnimationData data) {
            NetworkHelper.writeString(buf,data.currentAnimation.getName());
            buf.writeLong(data.currentAnimationTime);
        }

        @Override public AnimationData read(PacketBuffer buf) {
            return new NightTerrorEntity.AnimationData(buf);
        }

        @Override public DataParameter<AnimationData> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Override public AnimationData copyValue(AnimationData data) {
            return data.makeCopy();
        }
    },"animation_data_serializer");
    
    @SuppressWarnings("NullableProblems")
    public static final DataSerializerEntry CLASS_SERIALIZER = makeEntry(new DataSerializer<Class<?>>() {
        @Override public void write(PacketBuffer buf, Class<?> clazz) {
            NetworkHelper.writeString(buf,clazz.getName());
        }

        @Override public @Nullable Class<?> read(PacketBuffer buf) {
            String className = NetworkHelper.readString(buf);
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch(ClassNotFoundException ex) {
                LOGGER.error("Failed to read class from name {} in serializer!",className);
            }
            return clazz;
        }

        @Override public DataParameter<Class<?>> createKey(int id) {
            return new DataParameter<>(id,this);
        }

        @Override public Class<?> copyValue(Class<?> clazz) {
            return clazz;
        }
    },"class_serializer");

    private static DataSerializerEntry makeEntry(DataSerializer<?> serializer, String name) {
        DataSerializerEntry entry = new DataSerializerEntry(serializer);
        entry.setRegistryName(SleepLessRef.res(name));
        ALL_SERIALIZERS.add(entry);
        return entry;
    }

    public static DataSerializerEntry[] getSerializers() {
        return ALL_SERIALIZERS.toArray(new DataSerializerEntry[0]);
    }
}