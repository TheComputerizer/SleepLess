package mods.thecomputerizer.sleepless.registry.entities.nightterror;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.network.PacketToClient;
import mods.thecomputerizer.sleepless.network.PacketUpdateNightTerrorClient;
import mods.thecomputerizer.sleepless.network.PacketSendWorldSound;
import mods.thecomputerizer.sleepless.network.SleepLessNetwork;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

import static mods.thecomputerizer.sleepless.config.SleepLessConfig.NIGHT_TERROR;
import static mods.thecomputerizer.sleepless.core.SleepLessRef.MODID;
import static mods.thecomputerizer.sleepless.registry.PotionRegistry.INSOMNIA;
import static mods.thecomputerizer.sleepless.registry.SoundRegistry.BELL_REVERSE_SOUND;
import static mods.thecomputerizer.sleepless.registry.SoundRegistry.BELL_SOUND;
import static mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity.AnimationType.DEATH;
import static net.minecraft.util.SoundCategory.MASTER;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.world.BossInfo.Color.RED;
import static net.minecraft.world.BossInfo.Overlay.NOTCHED_10;

public class NightTerror {

    private static final int FINISH_BELLS = 1540;
    private static final int START_BELLS = FINISH_BELLS-(11*60);
    private final WorldServer world;
    private int activeTicks;
    private float bellVolume;
    private int maxColIndex;
    private NightTerrorEntity entity;
    private int endingTicks;
    private BossInfoServer bossBar;

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
        this.entity = this.activeTicks>FINISH_BELLS ?
                (NightTerrorEntity)this.world.getEntityByID(tag.getInteger("entityID")) : null;
        if(this.maxColIndex==11 && Objects.nonNull(this.entity)) instantiateBossBar();
    }

    public void onTick() {
        if(hasValidPlayer()) {
            if(this.activeTicks==0) initialize();
            else if(this.activeTicks>=START_BELLS && this.activeTicks<=FINISH_BELLS) {
                boolean isThirdSecond = this.activeTicks==START_BELLS || (this.activeTicks-START_BELLS)%60==0;
                if(isThirdSecond) {
                    sendBellMessage();
                    onBell(false);
                    if(this.activeTicks==FINISH_BELLS) onFinalBell();
                }
            } else if(this.activeTicks>FINISH_BELLS) {
                sendUpdate(player -> player.addPotionEffect(new PotionEffect(INSOMNIA,60)));
                if(Objects.nonNull(this.entity)) {
                    if(Objects.nonNull(this.bossBar)) this.bossBar.setPercent(this.entity.getHealth()/this.entity.getMaxHealth());
                    if(this.entity.getAnimationData().currentAnimation==DEATH && !this.entity.isDead) {
                        if(this.endingTicks==0) SoundUtil.playRemoteGlobalSound(true,this.world,
                                BELL_REVERSE_SOUND,MASTER,1f,1f);
                        this.endingTicks++;
                        float ending = 1f-(((float)(DEATH.getTotalTime()-this.endingTicks))/
                                           ((float)DEATH.getTotalTime()));
                        sendWorldPacket(new PacketUpdateNightTerrorClient(true,20f,1f,
                                ending,this.maxColIndex,false));
                    }
                    if(this.entity.isDead) CapabilityHandler.finishNightTerror(this.world);
                } else CapabilityHandler.finishNightTerror(this.world);
            }
            this.activeTicks++;
        }
    }

    private void initialize() {
        sendWorldPacket(new PacketUpdateNightTerrorClient(true,0f,0f,0f,-1,false));
        sendUpdate(player -> player.sendStatusMessage(createMessage(new Style().setColor(DARK_RED),
                lang("start")),true));
        onBell(true);
    }

    private void onFinalBell() {
        long time = this.world.getWorldTime()%24000L;
        if(time<18000L) this.world.setWorldTime(this.world.getWorldTime()+(18000L-time));
        else if(time>18000L) this.world.setWorldTime(this.world.getWorldTime()-(time-18000L));
        EntityPlayer player = this.world.playerEntities.get(this.world.rand.nextInt(this.world.playerEntities.size()));
        this.entity = new NightTerrorEntity(this.world);
        this.entity.setPosition(player.posX,player.posY+20d,player.posZ);
        this.world.spawnEntity(this.entity);
        instantiateBossBar();
    }

    private void instantiateBossBar() {
        this.bossBar = new BossInfoServer(this.entity.getDisplayName(),RED,NOTCHED_10);
        sendUpdate(player -> this.bossBar.addPlayer((EntityPlayerMP)player));
    }

    private void onBell(boolean isInit) {
        this.maxColIndex = this.activeTicks<START_BELLS ? -1 : Math.min((this.activeTicks-START_BELLS)/60,11);
        if(!isInit) {
            float fog = 0f;
            float color = 0f;
            if(this.maxColIndex==11) {
                fog = 20f;
                color = 1f;
            }
            sendWorldPacket(new PacketUpdateNightTerrorClient(true,fog,color,0f,this.maxColIndex,false));
        }
        sendWorldPacket(new PacketSendWorldSound(BELL_SOUND,MASTER,this.bellVolume, 1f));
        this.bellVolume+=0.05f;
    }

    private void sendBellMessage() {
        int offset = this.activeTicks-START_BELLS-120;
        if(offset<0 || (offset>0 && offset%180!=0)) return;
        final int index = offset==0 ? 1 : 1+(offset/180);
        sendUpdate(player -> player.sendStatusMessage(createMessage(new Style().setColor(DARK_RED)
                .setItalic(true),lang("toolate"+index)),true));
    }

    private String lang(String extra) {
        return "nightterror."+MODID+"."+extra;
    }

    private ITextComponent createMessage(@Nullable Style style, String langKey, Object ... args) {
        ITextComponent text = new TextComponentTranslation(langKey,args);
        return Objects.nonNull(style) ? text.setStyle(style) : text;
    }

    public void finish() {
        sendWorldPacket(new PacketUpdateNightTerrorClient(false,0f,0f,0f,-1,false));
        if(Objects.nonNull(this.bossBar)) this.bossBar.setVisible(false);
    }

    private void sendWorldPacket(PacketToClient packet) {
        SleepLessNetwork.sendToWorld(this.world,packet);
    }

    private void sendUpdate(Consumer<EntityPlayer> perPlayer) {
        for(EntityPlayer player : this.world.playerEntities) perPlayer.accept(player);
    }

    private boolean hasValidPlayer() {
        for(EntityPlayer player : this.world.playerEntities)
            if(CapabilityHandler.getSleepDebt(player)>=NIGHT_TERROR.minSleepDebt)
                return true;
        return false;
    }

    public boolean shoudlDaylightCycle() {
        return this.activeTicks<FINISH_BELLS;
    }

    public void catchUpJoiningPlayer(EntityPlayerMP player) {
        float ending = 1f-(((float)(DEATH.getTotalTime()-this.endingTicks))/
                           ((float)DEATH.getTotalTime()));
        float fog = 0f;
        float color = 0f;
        if(this.maxColIndex==11) {
            fog = 20f;
            color = 1f;
        }
        SleepLessNetwork.sendToClient(new PacketUpdateNightTerrorClient(
                true,fog,color,ending,this.maxColIndex,true),player);
        if(Objects.nonNull(this.bossBar)) this.bossBar.addPlayer(player);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound instanceTag = new NBTTagCompound();
        instanceTag.setInteger("worldDimension",this.world.provider.getDimension());
        instanceTag.setInteger("activeTicks",this.activeTicks);
        instanceTag.setFloat("bellVolume",this.bellVolume);
        instanceTag.setInteger("maxColumnIndex",this.maxColIndex);
        if(Objects.nonNull(this.entity)) instanceTag.setInteger("entityID",this.entity.getEntityId());
        tag.setTag("instance",instanceTag);
        return tag;
    }
}