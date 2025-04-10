package mods.thecomputerizer.sleepless.network;

import mods.thecomputerizer.theimpossiblelibrary.api.network.NetworkHandler;
import mods.thecomputerizer.theimpossiblelibrary.api.network.NetworkHelper;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageAPI;
import mods.thecomputerizer.theimpossiblelibrary.api.network.message.MessageWrapperAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.LOGGER;

public class SleepLessNetwork {
    
    public static void initClient() {}
    
    public static void initCommon() {
        NetworkHandler.registerMsgToClient(PacketRenderTests.class, PacketRenderTests::new);
        NetworkHandler.registerMsgToClient(PacketUpdateClientEffects.class,PacketUpdateClientEffects::new);
        NetworkHandler.registerMsgToClient(PacketUpdateNightTerrorClient.class,PacketUpdateNightTerrorClient::new);
        NetworkHandler.registerMsgToClient(PacketSendWorldSound.class,PacketSendWorldSound::new);
    }
    
    @SuppressWarnings("unchecked")
    public static void sendToClient(MessageAPI<?> msg, EntityPlayer... players) {
        if(Objects.isNull(msg) || Objects.isNull(players) || players.length==0) return;
        MessageWrapperAPI<?,?> wrapper = NetworkHelper.wrapMessage(NetworkHelper.getDirToClient(), msg);
        if(Objects.isNull(wrapper)) {
            LOGGER.error("Failed to wrap message from server {}",msg);
            return;
        }
        ((MessageWrapperAPI<EntityPlayer,?>)wrapper).setPlayers(players);
        wrapper.send();
    }
    
    @SuppressWarnings("unchecked")
    public static void sendToClient(MessageAPI<?> msg, Collection<EntityPlayer> players) {
        if(Objects.isNull(msg) || Objects.isNull(players) || players.isEmpty()) return;
        MessageWrapperAPI<?,?> wrapper = NetworkHelper.wrapMessage(NetworkHelper.getDirToClient(),msg);
        if(Objects.isNull(wrapper)) {
            LOGGER.error("Failed to wrap message from server {}",msg);
            return;
        }
        ((MessageWrapperAPI<EntityPlayer,?>)wrapper).setPlayers(players);
        wrapper.send();
    }
    
    public static void sendToServer(MessageAPI<?> msg) {
        if(Objects.isNull(msg)) return;
        MessageWrapperAPI<?,?> wrapper = NetworkHelper.wrapMessage(NetworkHelper.getDirToServer(),msg);
        if(Objects.isNull(wrapper)) {
            LOGGER.error("Failed to wrap message from client {}",msg);
            return;
        }
        wrapper.send();
    }
    
    public static void sendToWorld(@Nullable World world, MessageAPI<?> msg) {
        if(Objects.isNull(world) || world.isRemote) return;
        List<EntityPlayer> players = world.playerEntities;
        if(Objects.nonNull(players)) sendToClient(msg,new ArrayList<>(players));
    }
}