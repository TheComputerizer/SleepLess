package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.ClientPacketHandlers;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageAPI;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRenderTests extends PacketToClient {

    private final Vec3d posVec;
    private final Vec3d rotVec;
    private final int ticks;

    public PacketRenderTests(Vec3d posVec, Vec3d rotVec, int ticks) {
        super();
        this.posVec = posVec;
        this.rotVec = rotVec;
        this.ticks = ticks;
    }
    
    public PacketRenderTests(ByteBuf buf) {
        super(buf);
        this.posVec = readVec(buf);
        this.rotVec = readVec(buf);
        this.ticks = buf.readInt();
    }
    
    public void encode(ByteBuf buf) {
        writeVec(this.posVec,buf);
        writeVec(this.rotVec,buf);
        buf.writeInt(this.ticks);
    }

    @Override public MessageAPI<MessageContext> handle(MessageContext ctx) {
        ClientPacketHandlers.testColumnRender(this.posVec);
        return null;
    }
}