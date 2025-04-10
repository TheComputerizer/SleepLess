package mods.thecomputerizer.sleepless.util;

import mods.thecomputerizer.sleepless.network.PacketSendWorldSound;
import mods.thecomputerizer.sleepless.network.SleepLessNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

import static net.minecraft.client.audio.ISound.AttenuationType.LINEAR;
import static net.minecraft.client.audio.ISound.AttenuationType.NONE;
import static net.minecraft.util.SoundCategory.MUSIC;
import static net.minecraft.util.math.Vec3d.ZERO;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class SoundUtil {

    public static void playRemoteEntitySound(EntityLivingBase entity, SoundEvent event, boolean isMusic, float vol,
            float pitch) {
        if(Objects.nonNull(entity) && Objects.nonNull(entity.world) && entity.world.isRemote)
            playSound(makeEntitySound(entity,event,isMusic,vol,pitch));
    }

    public static void playRemoteGlobalSound(boolean shouldSendPacket, World world, SoundEvent event,
                                             SoundCategory category, float vol, float pitch) {
        playRemoteWorldSound(shouldSendPacket,world,event,category,vol,pitch,false,ZERO);
    }

    public static void playRemoteWorldSound(boolean shouldSendPacket, World world, SoundEvent event,
            SoundCategory category,
                                            float vol, float pitch, boolean isPositioned, Vec3d pos) {
        if(Objects.nonNull(world)) {
            if(world.isRemote) playSound(makeSound(event, category, vol, pitch, isPositioned, pos));
            else if(shouldSendPacket)
                SleepLessNetwork.sendToWorld(world,new PacketSendWorldSound(event,category,vol,pitch,isPositioned,pos));
        }
    }

    @SideOnly(CLIENT)
    public static void playPacketSound(SoundEvent sound, SoundCategory category, float vol, float pitch,
                                       boolean isPositioned, Vec3d pos) {
        playRemoteWorldSound(false,Minecraft.getMinecraft().world,sound,category,vol,pitch,isPositioned,pos);
    }

    @SideOnly(CLIENT)
    public static void playSound(PositionedSoundRecord sound) {
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }

    @SideOnly(CLIENT)
    public static PositionedSoundRecord makeGobalSound(SoundEvent event, SoundCategory category, float vol, float pitch) {
        return makeSound(event,category,vol,pitch,false,ZERO);
    }

    @SideOnly(CLIENT)
    public static PositionedSoundRecord makeEntitySound(EntityLivingBase entity, SoundEvent event, boolean isMusic,
            float vol, float pitch) {
        return makeSound(event,(isMusic ? MUSIC : entity.getSoundCategory()),vol,
                pitch,true,entity.getPositionVector());
    }

    @SideOnly(CLIENT)
    public static PositionedSoundRecord makeSound(SoundEvent event, SoundCategory category, float vol, float pitch,
            boolean isPositioned, Vec3d pos) {
        return new PositionedSoundRecord(event.getSoundName(),category,vol,pitch,false,0,
                (isPositioned ? LINEAR : NONE),(float)pos.x,(float)pos.y,(float)pos.z);
    }
}