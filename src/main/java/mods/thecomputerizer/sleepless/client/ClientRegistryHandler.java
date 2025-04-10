package mods.thecomputerizer.sleepless.client;

import mods.thecomputerizer.sleepless.client.render.RenderNightTerror;
import mods.thecomputerizer.sleepless.client.render.RenderPhantomEntity;
import mods.thecomputerizer.sleepless.registry.entities.nightterror.NightTerrorEntity;
import mods.thecomputerizer.sleepless.registry.entities.phantom.PhantomEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.MODID;
import static mods.thecomputerizer.sleepless.registry.ItemRegistry.TESSERACT;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@ParametersAreNonnullByDefault @SuppressWarnings("SameParameterValue")
@EventBusSubscriber(modid=MODID,value=CLIENT)
public final class ClientRegistryHandler {

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        registerItemModels();
        registerEntityRenderers();
    }

    private static void registerItemModels() {
        registerItemModel(TESSERACT,0,"inventory");
    }

    private static void registerItemModel(Item item, int meta, String modelState) {
        ModelResourceLocation modelResource = getModelResource(item, modelState);
        if(Objects.nonNull(modelResource))
            ModelLoader.setCustomModelResourceLocation(item,meta,modelResource);
    }

    private static ModelResourceLocation getModelResource(IForgeRegistryEntry.Impl<?> entry, String modelState) {
        ResourceLocation regName = entry.getRegistryName();
        if(Objects.isNull(regName)) return null;
        return new ModelResourceLocation(regName,modelState);
    }

    private static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(PhantomEntity.class,RenderPhantomEntity::new);
        RenderingRegistry.registerEntityRenderingHandler(NightTerrorEntity.class,RenderNightTerror::new);
    }
}