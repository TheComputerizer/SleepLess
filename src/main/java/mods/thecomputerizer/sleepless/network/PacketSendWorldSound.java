package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import mods.thecomputerizer.theimpossiblelibrary.api.network.NetworkHelper;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static mods.thecomputerizer.sleepless.client.render.ClientEffects.SCREEN_SHAKE;
import static net.minecraft.util.math.Vec3d.ZERO;
import static net.minecraftforge.fml.common.registry.ForgeRegistries.SOUND_EVENTS;

public class PacketSendWorldSound extends PacketToClient {

    private final SoundEvent sound;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;
    private final boolean isPositioned;
    private final Vec3d pos;

    public PacketSendWorldSound(SoundEvent sound, SoundCategory category, float vol, float pitch) {
        this(sound,category,vol,pitch,false,ZERO);
    }

    public PacketSendWorldSound(SoundEvent sound, SoundCategory category, float vol, float pitch, boolean hasPos,
            Vec3d pos) {
        super();
        this.sound = sound;
        this.category = category;
        this.volume = vol;
        this.pitch = pitch;
        this.isPositioned = hasPos;
        this.pos = pos;
    }

    public PacketSendWorldSound(ByteBuf buf) {
        super(buf);
        this.sound = SOUND_EVENTS.getValue(new ResourceLocation(NetworkHelper.readString(buf)));
        this.category = SoundCategory.getByName(NetworkHelper.readString(buf));
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.isPositioned = buf.readBoolean();
        this.pos = readVec(buf);
    }

    @Override public void encode(ByteBuf buf) {
        NetworkHelper.writeString(buf,this.sound.getSoundName().toString());
        NetworkHelper.writeString(buf,this.category.getName());
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
        buf.writeBoolean(this.isPositioned);
        writeVec(this.pos,buf);
    }
    
    @Override public MessageAPI<MessageContext> handle(MessageContext messageContext) {
        SoundUtil.playPacketSound(this.sound,this.category,this.volume,this.pitch,this.isPositioned,this.pos);
        SCREEN_SHAKE = this.volume*2;
        return null;
    }
}