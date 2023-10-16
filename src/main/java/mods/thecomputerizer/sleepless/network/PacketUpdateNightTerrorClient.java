package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.world.nightterror.NightTerrorClient;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketUpdateNightTerrorClient extends MessageImpl {

    private boolean silenceMusic;
    private float fogOverride;
    private float colorOverride;
    private float endingOverride;
    private int columnIndex;
    private boolean isCatchUp;

    public PacketUpdateNightTerrorClient() {}

    public PacketUpdateNightTerrorClient(boolean silenceMusic, float fogOverride, float colorOverride,
                                         float endingOverride, int columnIndex, boolean isCatchUp) {
        this.silenceMusic = silenceMusic;
        this.fogOverride = fogOverride;
        this.colorOverride = colorOverride;
        this.endingOverride = endingOverride;
        this.columnIndex = columnIndex;
        this.isCatchUp = isCatchUp;
    }

    @Override
    public IMessage handle(MessageContext messageContext) {
        NightTerrorClient.setClientEffect(this.silenceMusic,this.fogOverride,this.colorOverride,this.endingOverride,
                this.columnIndex,this.isCatchUp);
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.silenceMusic = buf.readBoolean();
        this.fogOverride = buf.readFloat();
        this.colorOverride = buf.readFloat();
        this.endingOverride = buf.readFloat();
        this.columnIndex = buf.readInt();
        this.isCatchUp = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.silenceMusic);
        buf.writeFloat(this.fogOverride);
        buf.writeFloat(this.colorOverride);
        buf.writeFloat(this.endingOverride);
        buf.writeInt(this.columnIndex);
        buf.writeBoolean(this.isCatchUp);
    }
}
