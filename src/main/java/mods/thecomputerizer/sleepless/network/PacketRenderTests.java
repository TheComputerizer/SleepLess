package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.ClientPacketHandlers;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketRenderTests extends MessageImpl {

    private Vec3d posVec;
    private Vec3d rotVec;
    private int ticks;

    public PacketRenderTests() {}

    public PacketRenderTests(Vec3d posVec, Vec3d rotVec, int ticks) {
        this.posVec = posVec;
        this.rotVec = rotVec;
        this.ticks = ticks;
    }

    @Override
    public IMessage handle(MessageContext ctx) {
        ClientPacketHandlers.testColumnRender(this.posVec);
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posVec = readVec(buf);
        this.rotVec = readVec(buf);
        this.ticks = buf.readInt();
    }

    private Vec3d readVec(ByteBuf buf) {
        return new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVec(buf,this.posVec);
        writeVec(buf,this.rotVec);
        buf.writeInt(this.ticks);
    }

    private void writeVec(ByteBuf buf, Vec3d vec) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }
}
