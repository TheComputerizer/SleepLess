package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("SameParameterValue")
public final class BlockRegistry {

    private static final List<Block> ALL_BLOCKS = new ArrayList<>();

    private static Block makeBlock(final String name, final Supplier<Block> constructor, final Consumer<Block> config) {
        final Block block = constructor.get();
        config.accept(block);
        block.setRegistryName(Constants.MODID, name);
        block.setTranslationKey(Constants.MODID+"."+name);
        ALL_BLOCKS.add(block);
        return block;
    }

    public static Block[] getBlocks() {
        return ALL_BLOCKS.toArray(new Block[0]);
    }
}
