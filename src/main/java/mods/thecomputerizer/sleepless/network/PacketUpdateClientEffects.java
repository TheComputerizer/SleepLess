package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.ClientPacketHandlers;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageAPI;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateClientEffects extends PacketToClient {

    private final float grayscale;
    private final float ambientChance;
    private final float quietSounds;
    private final float lightDim;
    private final float fogDensity;
    private final float walkSpeed;
    private final float breathingFactor;
    private final float miningSpeed;
    private final float phantomVisibility;

    public PacketUpdateClientEffects(float grayscale, float ambientChance, float quietSounds, float lightDim,
            float fogDensity, float walkSpeed, float breathingFactor, float miningSpeed, float phantomVisibility) {
        super();
        this.grayscale = grayscale;
        this.ambientChance = ambientChance;
        this.quietSounds = quietSounds;
        this.lightDim = lightDim;
        this.fogDensity = fogDensity;
        this.walkSpeed = walkSpeed;
        this.breathingFactor = breathingFactor;
        this.miningSpeed = miningSpeed;
        this.phantomVisibility = phantomVisibility;
    }
    
    public PacketUpdateClientEffects(ByteBuf buf) {
        super(buf);
        this.grayscale = buf.readFloat();
        this.ambientChance = buf.readFloat();
        this.quietSounds = buf.readFloat();
        this.lightDim = buf.readFloat();
        this.fogDensity = buf.readFloat();
        this.walkSpeed = buf.readFloat();
        this.breathingFactor = buf.readFloat();
        this.miningSpeed = buf.readFloat();
        this.phantomVisibility = buf.readFloat();
    }
    
    @Override public void encode(ByteBuf buf) {
        buf.writeFloat(this.grayscale);
        buf.writeFloat(this.ambientChance);
        buf.writeFloat(this.quietSounds);
        buf.writeFloat(this.lightDim);
        buf.writeFloat(this.fogDensity);
        buf.writeFloat(this.walkSpeed);
        buf.writeFloat(this.breathingFactor);
        buf.writeFloat(this.miningSpeed);
        buf.writeFloat(this.phantomVisibility);
    }

    @Override public MessageAPI<MessageContext> handle(MessageContext ctx) {
        ClientPacketHandlers.updateClientEffects(this.grayscale,this.ambientChance,this.quietSounds,this.lightDim,
                this.fogDensity,this.walkSpeed,this.breathingFactor,this.miningSpeed,this.phantomVisibility);
        return null;
    }
}