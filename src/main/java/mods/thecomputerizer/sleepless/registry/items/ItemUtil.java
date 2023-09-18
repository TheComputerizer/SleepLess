package mods.thecomputerizer.sleepless.registry.items;

import mods.thecomputerizer.sleepless.core.Constants;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUtil extends Item {

    protected NBTTagCompound getTag(ItemStack stack) {
        if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    @SideOnly(Side.CLIENT)
    protected String getTranslationForType(String type, String name) {
        return I18n.format(type + "." + Constants.MODID + "." + name + ".name");
    }
}
