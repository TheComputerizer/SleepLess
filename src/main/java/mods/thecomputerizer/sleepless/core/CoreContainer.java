package mods.thecomputerizer.sleepless.core;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class CoreContainer extends DummyModContainer {

    public CoreContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "sleeplesscore";
        meta.name = "SleepLess Core";
        meta.description = "Vanilla mixin loader for SleepLess";
        meta.version = Constants.VERSION;
        meta.authorList.add("The_Computerizer");
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}