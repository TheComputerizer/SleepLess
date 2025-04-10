package mods.thecomputerizer.sleepless.registry.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.MODID;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ItemUtil extends Item {

    protected static NBTTagCompound getTag(ItemStack stack) {
        if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    @SideOnly(CLIENT)
    protected String getTranslationForType(String type, String name) {
        return I18n.format(type+"."+MODID+"."+name+".name");
    }
}