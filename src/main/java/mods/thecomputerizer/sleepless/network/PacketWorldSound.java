package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import mods.thecomputerizer.theimpossiblelibrary.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketWorldSound extends MessageImpl {

    private ResourceLocation soundLocation;
    private SoundCategory category;
    private float volume;
    private float pitch;

    public PacketWorldSound() {}

    public PacketWorldSound(ResourceLocation soundLocation, SoundCategory category, float volume, float pitch) {
        this.soundLocation = soundLocation;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }
    @Override
    public IMessage handle(MessageContext messageContext) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(this.soundLocation,this.category,
                this.volume,this.pitch,false,0, ISound.AttenuationType.NONE,0,0,0));
        ClientEffects.SCREEN_SHAKE = this.volume*2;
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.soundLocation = NetworkUtil.readResourceLocation(buf);
        this.category = SoundCategory.getByName(NetworkUtil.readString(buf));
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.writeResourceLocation(buf,this.soundLocation);
        NetworkUtil.writeString(buf,this.category.getName());
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }
}
