package mods.thecomputerizer.sleepless.client.render.geometry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITickableGeometry<T> {

    T setTime(int time);
    T init();
    boolean isInitialized();
    void onTick();
    T reset();
}
