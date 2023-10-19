package mods.thecomputerizer.sleepless.registry;

import mcp.MethodsReturnNonnullByDefault;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import mods.thecomputerizer.theimpossiblelibrary.Constants;
import mods.thecomputerizer.theimpossiblelibrary.util.NetworkUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DataSerializerRegistry {

    private static final List<DataSerializerEntry> ALL_SERIALIZERS = new ArrayList<>();
    public static final DataSerializerEntry ANIMATION_SERIALIZER = makeEntry(new DataSerializer<NightTerrorEntity.AnimationData>() {
        @Override
        public void write(PacketBuffer buf, NightTerrorEntity.AnimationData data) {
            NetworkUtil.writeString(buf,data.currentAnimation.getName());
            buf.writeLong(data.currentAnimationTime);
        }

        @Override
        public NightTerrorEntity.AnimationData read(PacketBuffer buf) {
            return new NightTerrorEntity.AnimationData(buf);
        }

        @Override
        public DataParameter<NightTerrorEntity.AnimationData> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Override
        public NightTerrorEntity.AnimationData copyValue(NightTerrorEntity.AnimationData data) {
            return data.makeCopy();
        }
    },"animation_data_serializer");

    private static DataSerializerEntry makeEntry(DataSerializer<?> serializer, String name) {
        DataSerializerEntry entry = new DataSerializerEntry(serializer);
        entry.setRegistryName(Constants.res(name));
        ALL_SERIALIZERS.add(entry);
        return entry;
    }

    public static DataSerializerEntry[] getSerializers() {
        return ALL_SERIALIZERS.toArray(new DataSerializerEntry[0]);
    }
}
