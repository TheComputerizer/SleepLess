package mods.thecomputerizer.sleepless.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The class itself shouldn't be annotated with SideOnly but all methods and parameters should be
 */
public class ClientPacketHandlers {

    @SideOnly(Side.CLIENT)
    public static void methodName() {

    }
}
