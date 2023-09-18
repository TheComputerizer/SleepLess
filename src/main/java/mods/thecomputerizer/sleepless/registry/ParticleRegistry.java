package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.client.particle.ParticleTest;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.theimpossiblelibrary.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SuppressWarnings("SameParameterValue")
@Mod.EventBusSubscriber(modid = Constants.MODID)
public final class ParticleRegistry {

    private static final Class<?>[] PARTICLE_INIT_CLASSES = {String.class, int.class, boolean.class};
    public static final EnumParticleTypes TEST_PARTICLE = registerParticle("TEST_PARTICLE",true);

    private static TextureAtlasSprite FONT_ATLAS = null;

    private static EnumParticleTypes registerParticle(String name, boolean ignoreRange) {
        String camelName = TextUtil.makeCaseTypeFromSnake(name,TextUtil.TextCasing.CAMEL);
        int id = EnumParticleTypes.values().length;
        Constants.LOGGER.info("Registrering particle with name {}",camelName);
        EnumParticleTypes ret = EnumHelper.addEnum(EnumParticleTypes.class,name,PARTICLE_INIT_CLASSES,camelName,id,ignoreRange);
        if(Objects.nonNull(ret)) {
            EnumParticleTypes.PARTICLES.put(ret.getParticleID(), ret);
            EnumParticleTypes.BY_NAME.put(ret.getParticleName(), ret);
        } else Constants.LOGGER.error("Failed to register particle {}!",camelName);
        return ret;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void stitchEvent(TextureStitchEvent.Pre ev) {
        String texPath = Minecraft.getMinecraft().fontRenderer.locationFontTexture.getPath();
        texPath = texPath.substring(0,texPath.lastIndexOf(".")).replace("textures/","");
        FONT_ATLAS = ev.getMap().registerSprite(new ResourceLocation(texPath));
    }

    @SideOnly(Side.CLIENT)
    public static void postInit() {
        ParticleManager manager = Minecraft.getMinecraft().effectRenderer;
        manager.registerParticle(TEST_PARTICLE.getParticleID(),new ParticleTest.Factory());
    }

    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite getFontAtlas() {
        return FONT_ATLAS;
    }
}
