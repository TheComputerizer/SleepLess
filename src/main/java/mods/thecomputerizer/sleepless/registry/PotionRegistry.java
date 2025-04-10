package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.SleepLessRef;
import mods.thecomputerizer.sleepless.registry.potions.SleepLessPotion;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.List;

import static mods.thecomputerizer.sleepless.core.SleepLessRef.MODID;

public class PotionRegistry {

    private static final List<Potion> ALL_POTIONS = new ArrayList<>();
    public static final Potion INSOMNIA = makePotion("insomnia",new SleepLessPotion(true,0));
    public static final Potion TIRED = makePotion("tired",new SleepLessPotion(true,0));
    public static final Potion PHASED = makePotion("phased",new SleepLessPotion(false,0));

    private static Potion makePotion(final String name, final Potion potion) {
        potion.setPotionName("potion."+MODID+"."+name);
        potion.setRegistryName(SleepLessRef.res(name));
        ALL_POTIONS.add(potion);
        return potion;
    }

    public static Potion[] getPotions() {
        return ALL_POTIONS.toArray(new Potion[0]);
    }
}