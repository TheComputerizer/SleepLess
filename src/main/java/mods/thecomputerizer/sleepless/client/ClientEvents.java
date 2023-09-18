package mods.thecomputerizer.sleepless.client;


import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Constants.MODID, value = Side.CLIENT)
public class ClientEvents {

    private static boolean shaderLoaded = false;
    private static boolean screenShakePositive = true;

    @SubscribeEvent
    public static void screenShakeUpdate(TickEvent.PlayerTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase==TickEvent.Phase.END && event.player==mc.player) {
            if(!shaderLoaded) {
                mc.entityRenderer.loadShader(ClientEffects.GRAYSCALE_SHADER);
                shaderLoaded = true;
            }
            if(ClientEffects.isScreenShaking()) {
                event.player.rotationPitch += ClientEffects.getScreenShake(screenShakePositive);
                screenShakePositive = !screenShakePositive;
            }
        }
    }
}
