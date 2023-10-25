package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestPhantomSize extends PacketToClient {

    public PacketRequestPhantomSize () {}
    @Override
    public IMessage handle(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}
