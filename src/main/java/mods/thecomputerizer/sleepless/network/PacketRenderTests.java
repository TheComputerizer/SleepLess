package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.ClientPacketHandlers;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRenderTests extends PacketToClient {

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
    public void fromBytes(ByteBuf buf) {
        this.posVec = readVec(buf);
        this.rotVec = readVec(buf);
        this.ticks = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVec(this.posVec,buf);
        writeVec(this.rotVec,buf);
        buf.writeInt(this.ticks);
    }
}
