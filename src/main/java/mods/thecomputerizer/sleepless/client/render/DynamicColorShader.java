package mods.thecomputerizer.sleepless.client.render;

import mods.thecomputerizer.sleepless.config.SleepLessConfigHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class DynamicColorShader extends Shader {
    public DynamicColorShader(IResourceManager manager, String name, Framebuffer bufferIn, Framebuffer bufferOut) throws IOException {
        super(manager,name,bufferIn,bufferOut);
    }

    @Override
    public void render(float partialTicks) {
        if(Objects.nonNull(Minecraft.getMinecraft().player)) {
            if(SleepLessConfigHelper.shouldLoseColor()) {
                this.getShaderManager().getShaderUniformOrDefault("Prominence").set(ClientEffects.COLOR_CORRECTION);
                this.getShaderManager().getShaderUniformOrDefault("ColorAdjust").set(1f - ClientEffects.COLOR_CORRECTION / 2f);
            }
            if(SleepLessConfigHelper.shouldDimLight())
                this.getShaderManager().getShaderUniformOrDefault("LumaAdjust").set(1f - ClientEffects.LIGHT_DIMMING);
        }
        super.render(partialTicks);
    }
}