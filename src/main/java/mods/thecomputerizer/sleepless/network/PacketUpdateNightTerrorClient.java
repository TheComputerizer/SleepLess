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
    private int columnIndex;

    public PacketUpdateNightTerrorClient() {}

    public PacketUpdateNightTerrorClient(boolean silenceMusic, float fogOverride, float colorOverride,
                                         int columnIndex) {
        this.silenceMusic = silenceMusic;
        this.fogOverride = fogOverride;
        this.colorOverride = colorOverride;
        this.columnIndex = columnIndex;
    }

    @Override
    public IMessage handle(MessageContext messageContext) {
        NightTerrorClient.setClientEffect(this.silenceMusic,this.fogOverride,this.colorOverride,this.columnIndex);
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
        this.columnIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.silenceMusic);
        buf.writeFloat(this.fogOverride);
        buf.writeFloat(this.colorOverride);
        buf.writeInt(this.columnIndex);
    }
}
