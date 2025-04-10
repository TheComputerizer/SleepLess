package mods.thecomputerizer.sleepless.registry.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;

import static net.minecraft.item.EnumRarity.EPIC;

public class EpicItem extends ItemUtil {

    @Override public @Nonnull IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return EPIC;
    }
}