package mods.thecomputerizer.sleepless.network;

import io.netty.buffer.ByteBuf;
import mods.thecomputerizer.sleepless.registry.entities.NightTerrorEntity;
import mods.thecomputerizer.theimpossiblelibrary.network.MessageImpl;
import mods.thecomputerizer.theimpossiblelibrary.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSendNightTerrorAnimtion extends MessageImpl {

    private int entityID;
    private String animationType;

    public PacketSendNightTerrorAnimtion() {}

    public PacketSendNightTerrorAnimtion(int entityID, String animationType) {
        this.entityID = entityID;
        this.animationType = animationType;
    }

    @Override
    public IMessage handle(MessageContext ctx) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(this.entityID);
        if(entity instanceof NightTerrorEntity) ((NightTerrorEntity)entity).getAnimationData().setAnimation(this.animationType);
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.animationType = NetworkUtil.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        NetworkUtil.writeString(buf,this.animationType);
    }
}
