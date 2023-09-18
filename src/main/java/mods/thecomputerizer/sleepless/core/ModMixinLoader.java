package mods.thecomputerizer.sleepless.core;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ModMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Stream.of("sleepless_mods.mixin.json").collect(Collectors.toList());
    }
}
