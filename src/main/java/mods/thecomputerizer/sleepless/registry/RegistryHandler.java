package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.tiles.TestBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

import static mods.thecomputerizer.sleepless.registry.ItemRegistry.TEST_ITEM;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public final class RegistryHandler {
    public static final CreativeTabs SLEEPLESS_TAB = new CreativeTabs(Constants.MODID) {
        @SideOnly(Side.CLIENT)
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(TEST_ITEM);
        }
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        register(event,BlockRegistry.getBlocks());
        GameRegistry.registerTileEntity(TestBlockEntity.class,Constants.res("tile.test_block"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        register(event,EntityRegistry.getEntityEntries());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        register(event,ItemRegistry.getItems());
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        register(event,PotionRegistry.getPotions());
    }

    @SubscribeEvent
    public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        register(event,SoundRegistry.getSounds());
    }

    @SafeVarargs
    private static <E extends IForgeRegistryEntry<E>> void register(RegistryEvent.Register<E> event, E ... toRegister) {
        event.getRegistry().registerAll(toRegister);
    }
}
