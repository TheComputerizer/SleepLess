package mods.thecomputerizer.sleepless.client;


import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.client.render.geometry.ITickableGeometry;
import mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender;
import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.PotionRegistry;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static mods.thecomputerizer.sleepless.client.render.geometry.StaticGeometryRender.STATIC_RENDERS;

@Mod.EventBusSubscriber(modid = Constants.MODID, value = Side.CLIENT)
public class ClientEvents {

    public static List<ITickableGeometry<?>> TICKABLE_GEOMETRY = new ArrayList<>();
    private static boolean shaderLoaded = false;
    private static boolean screenShakePositive = true;
    private static boolean breathingIn = true;
    private static int secondTimer = 0;

    @SubscribeEvent
    public static void onFovUpdate(FOVUpdateEvent event) {
        if(SleepLessConfigHelper.shouldBreatheHeavily())
            event.setNewfov(ClientEffects.getFOVAdjustment(event.getFov()));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase==TickEvent.Phase.END && event.player==mc.player) {
            if(!shaderLoaded) {
                mc.entityRenderer.loadShader(ClientEffects.GRAYSCALE_SHADER);
                shaderLoaded = true;
            }
            if(ClientEffects.SCREEN_SHAKE>0) {
                event.player.rotationPitch += ClientEffects.getScreenShake(screenShakePositive);
                screenShakePositive = !screenShakePositive;
                ClientEffects.SCREEN_SHAKE-=0.01f;
            }
            if(SleepLessConfigHelper.shouldBreatheHeavily() && ClientEffects.BREATHING_FACTOR>0) {
                int factor = breathingIn ? 1 : -1;
                float adder = (ClientEffects.BREATHING_FACTOR/100f)*factor;
                ClientEffects.FOV_ADJUST = ClientEffects.FOV_ADJUST+adder;
                if(ClientEffects.FOV_ADJUST<=0) breathingIn = true;
                else if(ClientEffects.FOV_ADJUST>=ClientEffects.BREATHING_FACTOR) breathingIn = false;
            }
            if(SleepLessConfigHelper.shouldPlaySounds()) {
                secondTimer++;
                if(secondTimer>19) {
                    ClientEffects.tryAmbientSound();
                    secondTimer = 0;
                }
            }
            if(Objects.nonNull(NightTerrorClient.GEOMETRY_RENDER))
                NightTerrorClient.GEOMETRY_RENDER.setRenderXZ(mc.player.posX,mc.player.posZ);
        }
    }

    @SubscribeEvent
    public static void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if(SleepLessConfigHelper.shouldIncreaseFog()) {
            event.setDensity(NightTerrorClient.overrideFog(event.getDensity()+(0.75f*ClientEffects.FOG_DENSITY)+10f*ClientEffects.SCREEN_SHAKE));
        }
    }

    @SubscribeEvent
    public static void onFogColor(EntityViewRenderEvent.FogColors event) {
        event.setBlue(NightTerrorClient.overrideNotRed(event.getBlue()*(1f-ClientEffects.SCREEN_SHAKE)));
        event.setGreen(NightTerrorClient.overrideNotRed(event.getGreen()*(1f-ClientEffects.SCREEN_SHAKE)));
        event.setRed(NightTerrorClient.overrideRed(event.getRed()+((1f-event.getRed())*ClientEffects.SCREEN_SHAKE)));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase==TickEvent.Phase.END && Objects.nonNull(Minecraft.getMinecraft().world)) {
            NightTerrorClient.checkRenderCache();
            Iterator<ITickableGeometry<?>> tickItr = TICKABLE_GEOMETRY.iterator();
            while(tickItr.hasNext()) {
                ITickableGeometry<?> tickable = tickItr.next();
                tickable.onTick();
                if(!tickable.isInitialized()) tickItr.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldLastEvent event) {
        if(ClientEffects.PHANTOM_VISIBILITY>0f) {
            synchronized(STATIC_RENDERS) {
                Iterator<StaticGeometryRender> renderItr = STATIC_RENDERS.iterator();
                while(renderItr.hasNext()) {
                    StaticGeometryRender staticRender = renderItr.next();
                    if(staticRender.isEmpty()) renderItr.remove();
                    else staticRender.render(event.getPartialTicks());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientDisconnted(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        NightTerrorClient.onClientDisconnected();
    }

    @SubscribeEvent
    public static void onPlayerSPPushOutOfBlocksEvent(PlayerSPPushOutOfBlocksEvent event) {
        if(event.getEntityPlayer().isPotionActive(PotionRegistry.PHASED)) event.setCanceled(true);
    }
}
