package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

public abstract class PacketToClient extends MessageImpl {

    protected PacketToClient() {}

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    protected Vec3d readVec(ByteBuf buf) {
        return new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
    }

    protected void writeVec(Vec3d vec, ByteBuf buf) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }
}
