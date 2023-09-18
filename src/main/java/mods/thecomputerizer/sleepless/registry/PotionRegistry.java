package mods.thecomputerizer.sleepless.registry;

import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.registry.potions.InsomniaPotion;
import mods.thecomputerizer.sleepless.registry.potions.TiredPotion;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

@GameRegistry.ObjectHolder(Constants.MODID)
public class PotionRegistry {

    private static final List<Potion> ALL_POTIONS = new ArrayList<>();
    public static final Potion INSOMNIA = makePotion("insomnia",new InsomniaPotion(true,0));
    public static final Potion TIRED = makePotion("tired",new TiredPotion(true,0));

    private static Potion makePotion(final String name, final Potion potion) {
        potion.setPotionName("potion." + Constants.MODID + "." + name);
        potion.setRegistryName(Constants.res(name));
        ALL_POTIONS.add(potion);
        return potion;
    }

    public static Potion[] getPotions() {
        return ALL_POTIONS.toArray(new Potion[0]);
    }
}
