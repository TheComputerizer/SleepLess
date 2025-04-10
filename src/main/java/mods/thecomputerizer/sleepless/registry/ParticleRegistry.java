package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.client.particle.ParticleTest;
import mods.thecomputerizer.theimpossiblelibrary.api.text.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.LOGGER;
import static mods.thecomputerizer.sleepless.core.SleepLessRef.MODID;
import static mods.thecomputerizer.theimpossiblelibrary.api.text.TextHelper.TextCasing.CAMEL;
import static net.minecraft.util.EnumParticleTypes.BY_NAME;
import static net.minecraft.util.EnumParticleTypes.PARTICLES;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SuppressWarnings("SameParameterValue")
@EventBusSubscriber(modid=MODID)
public final class ParticleRegistry {

    private static final Class<?>[] PARTICLE_INIT_CLASSES = {String.class, int.class, boolean.class};
    public static final EnumParticleTypes TEST_PARTICLE = registerParticle("TEST_PARTICLE",true);

    private static TextureAtlasSprite FONT_ATLAS = null;

    private static EnumParticleTypes registerParticle(String name, boolean ignoreRange) {
        String camelName = TextHelper.makeCaseTypeFromSnake(name,CAMEL);
        int id = EnumParticleTypes.values().length;
        LOGGER.info("Registrering particle with name {}", camelName);
        EnumParticleTypes ret = EnumHelper.addEnum(EnumParticleTypes.class,name,PARTICLE_INIT_CLASSES,camelName,id,ignoreRange);
        if(Objects.nonNull(ret)) {
            PARTICLES.put(ret.getParticleID(),ret);
            BY_NAME.put(ret.getParticleName(),ret);
        } else LOGGER.error("Failed to register particle {}!", camelName);
        return ret;
    }

    @SubscribeEvent @SideOnly(CLIENT)
    public static void stitchEvent(Pre ev) {
        String texPath = Minecraft.getMinecraft().fontRenderer.locationFontTexture.getPath();
        texPath = texPath.substring(0,texPath.lastIndexOf(".")).replace("textures/","");
        FONT_ATLAS = ev.getMap().registerSprite(new ResourceLocation(texPath));
    }

    @SideOnly(CLIENT)
    public static void postInit() {
        ParticleManager manager = Minecraft.getMinecraft().effectRenderer;
        manager.registerParticle(TEST_PARTICLE.getParticleID(),new ParticleTest.Factory());
    }

    @SideOnly(CLIENT)
    public static TextureAtlasSprite getFontAtlas() {
        return FONT_ATLAS;
    }
}