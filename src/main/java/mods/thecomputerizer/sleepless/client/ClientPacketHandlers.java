package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.client.render.ClientEffects;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The class itself shouldn't be annotated with SideOnly but all methods and parameters should be
 */
public class ClientPacketHandlers {

    @SideOnly(Side.CLIENT)
    public static void updateClientEffects(float grayscale) {
        ClientEffects.COLOR_CORRECTION = grayscale;
    }
}
