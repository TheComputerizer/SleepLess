package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.items.EpicItem;
import mods.thecomputerizer.sleepless.registry.items.TesseractItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static mods.thecomputerizer.sleepless.registry.RegistryHandler.SLEEPLESS_TAB;

@SuppressWarnings("SameParameterValue")
public final class ItemRegistry {

    private static final List<Item> ALL_ITEMS = new ArrayList<>();
    public static final Item TESSERACT = makeItem("tesseract", TesseractItem::new,
            item -> item.setCreativeTab(SLEEPLESS_TAB).setMaxStackSize(1));
    public static final Item TEST_ITEM_BLOCK = makeEpicItemBlock(BlockRegistry.TEST_BLOCK,
            item -> item.setCreativeTab(SLEEPLESS_TAB).setMaxStackSize(1));


    private static Item makeItem(final String name, final Supplier<Item> constructor, final Consumer<Item> config) {
        final Item item = constructor.get();
        config.accept(item);
        item.setTranslationKey(Constants.MODID+"."+name);
        item.setRegistryName(Constants.MODID, name);
        item.setMaxStackSize(1);
        ALL_ITEMS.add(item);
        return item;
    }

    private static ItemBlock makeEpicItemBlock(final @Nonnull Block constructor, final Consumer<ItemBlock> config) {
        final ItemBlock item = new ItemBlock(constructor);
        config.accept(item);
        item.setRegistryName(Objects.requireNonNull(constructor.getRegistryName()));
        item.setTranslationKey(constructor.getTranslationKey());
        ALL_ITEMS.add(item);
        return item;
    }
    public static Item[] getItems() {
        return ALL_ITEMS.toArray(new Item[0]);
    }
}
