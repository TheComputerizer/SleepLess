package mods.thecomputerizer.sleepless.registry.items;

import mcp.MethodsReturnNonnullByDefault;
import mods.thecomputerizer.sleepless.client.render.geometry.Convex3D;
import mods.thecomputerizer.sleepless.client.render.geometry.Shapes;
import mods.thecomputerizer.sleepless.client.render.geometry.Tesseract;
import mods.thecomputerizer.sleepless.util.SoundUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static mods.thecomputerizer.sleepless.registry.PotionRegistry.PHASED;
import static mods.thecomputerizer.sleepless.registry.SoundRegistry.BOOSTED_TP_REVERSE_SOUND;
import static mods.thecomputerizer.sleepless.registry.SoundRegistry.BOOSTED_TP_SOUND;
import static net.minecraft.util.EnumActionResult.SUCCESS;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
public class TesseractItem extends EpicItem {

    @SideOnly(CLIENT) private Tesseract renderer;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound tag = ItemUtil.getTag(stack);
        boolean isActive = tag.getBoolean("activeTesseract");
        isActive = !isActive;
        if(isActive) {
            player.addPotionEffect(new PotionEffect(PHASED,999999));
            SoundUtil.playRemoteEntitySound(player,BOOSTED_TP_SOUND,false,1f,0.6f);
            if(world.isRemote) setRendererRotation(1d);
        } else {
            player.removePotionEffect(PHASED);
            SoundUtil.playRemoteEntitySound(player,BOOSTED_TP_REVERSE_SOUND,false,1f,0.6f);
            if(world.isRemote) setRendererRotation(0.1d);
        }
        tag.setBoolean("activeTesseract",isActive);
        return new ActionResult<>(SUCCESS,player.getHeldItem(hand));
    }

    @SideOnly(CLIENT)
    private void makeDefaultRenderer() {
        Convex3D baseShape = Shapes.BOX.makeInstance();
        baseShape.setColor(0f,0f,0f,0.5f);
        baseShape.setRotationSpeed(0d,0.1d,0d);
        this.renderer = new Tesseract(baseShape);
        this.renderer.setScale(1/3f,1/3f,1/3f);
    }

    @SideOnly(CLIENT)
    private void setRendererRotation(double speed) {
        if(Objects.isNull(this.renderer)) makeDefaultRenderer();
        this.renderer.setRotations(0d,speed,0d);
    }

    @SideOnly(CLIENT)
    public Tesseract getRenderer() {
        if(Objects.isNull(this.renderer)) makeDefaultRenderer();
        return this.renderer;
    }
}