package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorClient;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageAPI;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateNightTerrorClient extends PacketToClient {

    private final boolean silenceMusic;
    private final float fogOverride;
    private final float colorOverride;
    private final float endingOverride;
    private final int columnIndex;
    private final boolean isCatchUp;

    public PacketUpdateNightTerrorClient(boolean silenceMusic, float fogOverride, float colorOverride,
            float endingOverride, int columnIndex, boolean isCatchUp) {
        super();
        this.silenceMusic = silenceMusic;
        this.fogOverride = fogOverride;
        this.colorOverride = colorOverride;
        this.endingOverride = endingOverride;
        this.columnIndex = columnIndex;
        this.isCatchUp = isCatchUp;
    }
    
    public PacketUpdateNightTerrorClient(ByteBuf buf) {
        super(buf);
        this.silenceMusic = buf.readBoolean();
        this.fogOverride = buf.readFloat();
        this.colorOverride = buf.readFloat();
        this.endingOverride = buf.readFloat();
        this.columnIndex = buf.readInt();
        this.isCatchUp = buf.readBoolean();
    }
    
    @Override public void encode(ByteBuf buf) {
        buf.writeBoolean(this.silenceMusic);
        buf.writeFloat(this.fogOverride);
        buf.writeFloat(this.colorOverride);
        buf.writeFloat(this.endingOverride);
        buf.writeInt(this.columnIndex);
        buf.writeBoolean(this.isCatchUp);
    }

    @Override public MessageAPI<MessageContext> handle(MessageContext ctx) {
        NightTerrorClient.setClientEffect(this.silenceMusic,this.fogOverride,this.colorOverride,this.endingOverride,
                this.columnIndex,this.isCatchUp);
        return null;
    }
}