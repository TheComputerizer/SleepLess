package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.ClientPacketHandlers;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketUpdateClientEffects extends MessageImpl {

    private float grayscale;
    private float ambientChance;
    private float quietSounds;
    private float lightDim;

    public PacketUpdateClientEffects() {}

    public PacketUpdateClientEffects(float grayscale, float ambientChance, float quietSounds, float lightDim) {
        this.grayscale = grayscale;
        this.ambientChance = ambientChance;
        this.quietSounds = quietSounds;
        this.lightDim = lightDim;
    }

    @Override
    public IMessage handle(MessageContext messageContext) {
        ClientPacketHandlers.updateClientEffects(this.grayscale,this.ambientChance,this.quietSounds,this.lightDim);
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.grayscale = buf.readFloat();
        this.ambientChance = buf.readFloat();
        this.quietSounds = buf.readFloat();
        this.lightDim = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.grayscale);
        buf.writeFloat(this.ambientChance);
        buf.writeFloat(this.quietSounds);
        buf.writeFloat(this.lightDim);
    }
}
