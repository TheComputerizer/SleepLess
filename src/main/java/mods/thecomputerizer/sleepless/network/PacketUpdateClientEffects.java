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
    private float fogDensity;
    private float walkSpeed;
    private float breathingFactor;
    private float miningSpeed;

    public PacketUpdateClientEffects() {}

    public PacketUpdateClientEffects(float grayscale, float ambientChance, float quietSounds, float lightDim,
                                     float fogDensity, float walkSpeed, float breathingFactor, float miningSpeed) {
        this.grayscale = grayscale;
        this.ambientChance = ambientChance;
        this.quietSounds = quietSounds;
        this.lightDim = lightDim;
        this.fogDensity = fogDensity;
        this.walkSpeed = walkSpeed;
        this.breathingFactor = breathingFactor;
        this.miningSpeed = miningSpeed;
    }

    @Override
    public IMessage handle(MessageContext messageContext) {
        ClientPacketHandlers.updateClientEffects(this.grayscale,this.ambientChance,this.quietSounds,this.lightDim,
                this.fogDensity,this.walkSpeed,this.breathingFactor,this.miningSpeed);
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
        this.fogDensity = buf.readFloat();
        this.walkSpeed = buf.readFloat();
        this.breathingFactor = buf.readFloat();
        this.miningSpeed = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.grayscale);
        buf.writeFloat(this.ambientChance);
        buf.writeFloat(this.quietSounds);
        buf.writeFloat(this.lightDim);
        buf.writeFloat(this.fogDensity);
        buf.writeFloat(this.walkSpeed);
        buf.writeFloat(this.breathingFactor);
        buf.writeFloat(this.miningSpeed);
    }
}
