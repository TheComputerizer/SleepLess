package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.client.ClientPacketHandlers;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketUpdateClientEffects extends MessageImpl {

    private float grayscale;

    public PacketUpdateClientEffects() {}

    public PacketUpdateClientEffects(float grayscale) {
        this.grayscale = grayscale;
    }

    @Override
    public IMessage handle(MessageContext messageContext) {
        ClientPacketHandlers.updateClientEffects(this.grayscale);
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.grayscale = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.grayscale);
    }
}
