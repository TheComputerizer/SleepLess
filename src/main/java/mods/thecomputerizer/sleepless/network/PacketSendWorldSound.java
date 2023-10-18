package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import mods.thecomputerizer.theimpossiblelibrary.util.NetworkUtil;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PacketSendWorldSound extends PacketToClient {

    private SoundEvent sound;
    private SoundCategory category;
    private float volume;
    private float pitch;
    private boolean isPositioned;
    private Vec3d pos;

    public PacketSendWorldSound() {}

    public PacketSendWorldSound(SoundEvent sound, SoundCategory category, float vol, float pitch) {
        this(sound,category,vol,pitch,false,Vec3d.ZERO);
    }

    public PacketSendWorldSound(SoundEvent sound, SoundCategory category, float vol, float pitch, boolean hasPos, Vec3d pos) {
        this.sound = sound;
        this.category = category;
        this.volume = vol;
        this.pitch = pitch;
        this.isPositioned = hasPos;
        this.pos = pos;
    }
    @Override
    public IMessage handle(MessageContext messageContext) {
        SoundUtil.playPacketSound(this.sound,this.category,this.volume,this.pitch,this.isPositioned,this.pos);
        ClientEffects.SCREEN_SHAKE = this.volume*2;
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sound = ForgeRegistries.SOUND_EVENTS.getValue(NetworkUtil.readResourceLocation(buf));
        this.category = SoundCategory.getByName(NetworkUtil.readString(buf));
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.isPositioned = buf.readBoolean();
        this.pos = readVec(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.writeResourceLocation(buf,this.sound.getSoundName());
        NetworkUtil.writeString(buf,this.category.getName());
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
        buf.writeBoolean(this.isPositioned);
        writeVec(this.pos,buf);
    }
}
