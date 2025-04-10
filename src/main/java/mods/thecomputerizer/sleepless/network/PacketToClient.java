package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageAPI;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class PacketToClient extends MessageAPI<MessageContext> {

    protected PacketToClient() {}
    
    protected PacketToClient(ByteBuf buf) {}

    protected Vec3d readVec(ByteBuf buf) {
        return new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
    }

    protected void writeVec(Vec3d vec, ByteBuf buf) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }
}