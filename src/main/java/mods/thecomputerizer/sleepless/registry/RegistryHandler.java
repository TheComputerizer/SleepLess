package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.SleepLessRef;
import mods.thecomputerizer.sleepless.registry.tiles.TestBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.MODID;
import static mods.thecomputerizer.sleepless.registry.ItemRegistry.TESSERACT;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@EventBusSubscriber(modid=MODID)
public final class RegistryHandler {
    
    public static final CreativeTabs SLEEPLESS_TAB = new CreativeTabs(MODID) {
        @SideOnly(CLIENT)
        public @Nonnull ItemStack createIcon() {
            return new ItemStack(TESSERACT);
        }
    };

    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
        register(event,BlockRegistry.getBlocks());
        GameRegistry.registerTileEntity(TestBlockEntity.class,SleepLessRef.res("tile.test_block"));
    }

    @SubscribeEvent
    public static void registerDataSerializers(Register<DataSerializerEntry> event) {
        register(event,DataSerializerRegistry.getSerializers());
    }

    @SubscribeEvent
    public static void registerEntities(Register<EntityEntry> event) {
        register(event,EntityRegistry.getEntityEntries());
    }

    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        register(event,ItemRegistry.getItems());
    }

    @SubscribeEvent
    public static void registerPotions(Register<Potion> event) {
        register(event,PotionRegistry.getPotions());
    }

    @SubscribeEvent
    public static void registerSoundEvents(Register<SoundEvent> event) {
        register(event,SoundRegistry.getSounds());
    }

    @SafeVarargs
    private static <E extends IForgeRegistryEntry<E>> void register(Register<E> event, E ... toRegister) {
        event.getRegistry().registerAll(toRegister);
    }
}