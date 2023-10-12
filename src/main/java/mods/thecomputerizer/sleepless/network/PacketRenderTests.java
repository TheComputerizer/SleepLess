package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.ClientPacketHandlers;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketRenderTests extends MessageImpl {

    private double x;
    private double y;
    private double z;
    private double xRot;
    private double yRot;
    private double zRot;
    private int ticks;

    public PacketRenderTests() {}

    public PacketRenderTests(double x, double y, double z, double xRot, double yRot, double zRot, int ticks) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
        this.ticks = ticks;
    }

    @Override
    public IMessage handle(MessageContext ctx) {
        ClientPacketHandlers.testColumnRender(this.x,this.y,this.z);
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.xRot = buf.readDouble();
        this.yRot = buf.readDouble();
        this.zRot = buf.readDouble();
        this.ticks = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeDouble(this.xRot);
        buf.writeDouble(this.yRot);
        buf.writeDouble(this.zRot);
        buf.writeInt(this.ticks);
    }
}
