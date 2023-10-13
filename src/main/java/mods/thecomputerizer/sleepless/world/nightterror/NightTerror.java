package mods.thecomputerizer.sleepless.world.nightterror;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.network.PacketUpdateNightTerrorClient;
import mods.thecomputerizer.sleepless.network.PacketWorldSound;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class NightTerror {

    public static NightTerror INSTANCE;
    private static int cooldown = 0;

    public static void checkInstance(WorldServer world) {
        long time = world.getWorldTime()%24000L;
        if(time<13000L) {
            if(Objects.nonNull(INSTANCE)) INSTANCE.finish();
            INSTANCE = null;
        } else {
            if(cooldown>0) cooldown--;
            if(Objects.isNull(INSTANCE) && cooldown<=0 && time<16000L) {
                cooldown = 300;
                List<Float> chances = new ArrayList<>();
                for(EntityPlayer player : world.playerEntities) {
                    float chance = SleepLessConfigHelper.nightTerrorChance((EntityPlayerMP)player);
                    if(chance>0) chances.add(chance);
                }
                if(!chances.isEmpty() && world.rand.nextFloat()<SleepLessConfigHelper.calculateFinalChance(chances))
                    INSTANCE = new NightTerror(world);
            }
        }
    }

    private final WorldServer world;
    private int activeTicks;
    private float bellVolume = 0.1f;

    private NightTerror(WorldServer world) {
        this.world = world;
    }

    public void onTick() {
        if(this.activeTicks==0) initialize();
        else if(this.activeTicks>=240) {
            if(this.activeTicks==360) sendUpdate(player -> player.sendStatusMessage(createMessage(new Style()
                    .setColor(TextFormatting.DARK_RED).setItalic(true),lang("toolate1")),true));
            if(this.activeTicks==540) sendUpdate(player -> player.sendStatusMessage(createMessage(new Style()
                    .setColor(TextFormatting.DARK_RED).setItalic(true),lang("toolate2")),true));
            if(this.activeTicks==720) sendUpdate(player -> player.sendStatusMessage(createMessage(new Style()
                    .setColor(TextFormatting.DARK_RED).setItalic(true),lang("toolate3")),true));
            if(this.activeTicks==900) sendUpdate(player -> player.sendStatusMessage(createMessage(new Style()
                    .setColor(TextFormatting.DARK_RED).setItalic(true),lang("toolate4")),true));
            if(this.activeTicks<=900 && (this.activeTicks==240 || (this.activeTicks-240)%60==0))
                playSound();
            if(this.activeTicks>=900) sendUpdate(player -> player.addPotionEffect(new PotionEffect(PotionRegistry.INSOMNIA,60)));
        }
        this.activeTicks++;
    }

    private void initialize() {
        PacketUpdateNightTerrorClient packet = new PacketUpdateNightTerrorClient(true,0f,0f,-1);
        sendUpdate(player -> {
            player.sendStatusMessage(createMessage(new Style()
                    .setColor(TextFormatting.DARK_RED),lang("start")),true);
            packet.addPlayers((EntityPlayerMP)player);
        });
        packet.send();
        playSound();
    }

    private void playSound() {
        int columnIndex = (int)((this.bellVolume-0.1f)*20f);
        float fog = 0f;
        float color = 0f;
        if(columnIndex>=12) {
            columnIndex = -1;
            fog = 10f;
            color = 1f;
        }
        PacketUpdateNightTerrorClient packet = new PacketUpdateNightTerrorClient(true,fog,color,columnIndex);
        PacketWorldSound packetSound = new PacketWorldSound(SoundRegistry.BELL_SOUND.getRegistryName(),SoundCategory.AMBIENT,this.bellVolume,1f);
        sendUpdate(player -> {
            EntityPlayerMP player1 = (EntityPlayerMP)player;
            packet.addPlayers(player1);
            packetSound.addPlayers(player1);
        });
        packet.send();
        packetSound.send();
        this.bellVolume+=0.05f;
    }

    private String lang(String extra) {
        return "nightterror."+Constants.MODID+"."+extra;
    }

    private ITextComponent createMessage(@Nullable Style style, String langKey, Object ... args) {
        ITextComponent text = new TextComponentTranslation(langKey,args);
        return Objects.nonNull(style) ? text.setStyle(style) : text;
    }

    private void finish() {
        PacketUpdateNightTerrorClient packet = new PacketUpdateNightTerrorClient(false,0f,0f,-1);
        sendUpdate(player -> {
            player.removePotionEffect(PotionRegistry.INSOMNIA);
            packet.addPlayers((EntityPlayerMP)player);
        });
        packet.send();
    }

    private void sendUpdate(Consumer<EntityPlayer> perPlayer) {
        for(EntityPlayer player : this.world.playerEntities) perPlayer.accept(player);
    }
}
