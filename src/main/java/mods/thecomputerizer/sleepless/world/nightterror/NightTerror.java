package mods.thecomputerizer.sleepless.world.nightterror;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.config.SleepLessConfig;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.network.PacketUpdateNightTerrorClient;
import mods.thecomputerizer.sleepless.network.PacketWorldSound;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.SoundRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class NightTerror {

    private static final int START_BELLS = 240;
    private static final int FINISH_BELLS = 240+(11*60);
    private final WorldServer world;
    private int activeTicks;
    private float bellVolume;
    private int maxColIndex;

    public NightTerror(WorldServer world) {
        this.world = world;
        this.activeTicks = 0;
        this.bellVolume = 0.1f;
        this.maxColIndex = -1;
    }

    public NightTerror(NBTTagCompound tag) {
        this.world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(tag.getInteger("worldDimension"));
        this.activeTicks = tag.getInteger("activeTicks");
        this.bellVolume = tag.getFloat("bellVolume");
        this.maxColIndex = tag.getInteger("maxColumnIndex");
    }

    public void onTick() {
        if(hasValidPlayer()) {
            if (this.activeTicks == 0) initialize();
            else if (this.activeTicks>=START_BELLS && this.activeTicks<=FINISH_BELLS) {
                boolean isThirdSecond = this.activeTicks == START_BELLS || (this.activeTicks-START_BELLS)%60==0;
                if(isThirdSecond) {
                    sendBellMessage();
                    onBell();
                    if(this.activeTicks==FINISH_BELLS) onFinalBell();
                }
            } else if(this.activeTicks>FINISH_BELLS) {
                sendUpdate(player -> player.addPotionEffect(new PotionEffect(PotionRegistry.INSOMNIA, 60)));
            }
            this.activeTicks++;
        }
    }

    private void initialize() {
        PacketUpdateNightTerrorClient packet = new PacketUpdateNightTerrorClient(true,0f,0f,-1,false);
        sendUpdate(player -> {
            player.sendStatusMessage(createMessage(new Style()
                    .setColor(TextFormatting.DARK_RED),lang("start")),true);
            packet.addPlayers((EntityPlayerMP)player);
        });
        packet.send();
        onBell();
    }

    private void onFinalBell() {
        long time = this.world.getWorldTime()%24000L;
        if(time<18000L) this.world.setWorldTime(this.world.getWorldTime()+(18000L-time));
        else if(time>18000L) this.world.setWorldTime(this.world.getWorldTime()-(time-18000L));
    }

    private void onBell() {
        this.maxColIndex = this.activeTicks<START_BELLS ? -1 : Math.min((this.activeTicks-START_BELLS)/60,11);
        float fog = 0f;
        float color = 0f;
        if(this.maxColIndex==11) {
            fog = 20f;
            color = 1f;
        }
        PacketUpdateNightTerrorClient packet = new PacketUpdateNightTerrorClient(true,fog,color,this.maxColIndex,false);
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

    private void sendBellMessage() {
        int offset = this.activeTicks-START_BELLS-120;
        if(offset<0 || (offset>0 && offset%180!=0)) return;
        final int index = offset==0 ? 1 : 1+(offset/180);
        sendUpdate(player -> player.sendStatusMessage(createMessage(new Style()
                .setColor(TextFormatting.DARK_RED).setItalic(true), lang("toolate"+index)), true));
    }

    private String lang(String extra) {
        return "nightterror."+Constants.MODID+"."+extra;
    }

    private ITextComponent createMessage(@Nullable Style style, String langKey, Object ... args) {
        ITextComponent text = new TextComponentTranslation(langKey,args);
        return Objects.nonNull(style) ? text.setStyle(style) : text;
    }

    public void finish() {
        PacketUpdateNightTerrorClient packet = new PacketUpdateNightTerrorClient(false,0f,0f,-1,false);
        sendUpdate(player -> {
            player.removePotionEffect(PotionRegistry.INSOMNIA);
            packet.addPlayers((EntityPlayerMP)player);
        });
        packet.send();
    }

    private void sendUpdate(Consumer<EntityPlayer> perPlayer) {
        for(EntityPlayer player : this.world.playerEntities) perPlayer.accept(player);
    }

    private boolean hasValidPlayer() {
        for(EntityPlayer player : this.world.playerEntities)
            if(CapabilityHandler.getSleepDebt((EntityPlayerMP)player)>=SleepLessConfig.NIGHT_TERROR.minSleepDebt)
                return true;
        return false;
    }

    public boolean shoudlDaylightCycle() {
        return this.activeTicks<FINISH_BELLS;
    }

    public void catchUpJoiningPlayer(EntityPlayerMP player) {
        float fog = 0f;
        float color = 0f;
        if(this.maxColIndex==11) {
            fog = 50f;
            color = 1f;
        }
        new PacketUpdateNightTerrorClient(true,fog,color,this.maxColIndex,true).addPlayers(player).send();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound instanceTag = new NBTTagCompound();
        instanceTag.setInteger("worldDimension",this.world.provider.getDimension());
        instanceTag.setInteger("activeTicks",this.activeTicks);
        instanceTag.setFloat("bellVolume",this.bellVolume);
        instanceTag.setInteger("maxColumnIndex",this.maxColIndex);
        tag.setTag("instance",instanceTag);
        return tag;
    }
}
