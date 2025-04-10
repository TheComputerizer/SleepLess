package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageAPI;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestPhantomSize extends PacketToClient {

    public PacketRequestPhantomSize () {
        super();
    }

    public PacketRequestPhantomSize(ByteBuf buf) {
        super(buf);
    }

    @Override public void encode(ByteBuf buf) {}
    
    @Override public MessageAPI<MessageContext> handle(MessageContext ctx) {
        return null;
    }
}