package me.marin.lockout.lockout.goals.brewing;

import me.marin.lockout.lockout.interfaces.ObtainPotionItemGoal;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class BrewSwiftnessPotionGoal extends ObtainPotionItemGoal {

    public BrewSwiftnessPotionGoal(String id, String data) {
        super(id, data);
    }

    private static final List<RegistryEntry<Potion>> POTION_LIST = List.of(Potions.SWIFTNESS, Potions.LONG_SWIFTNESS, Potions.STRONG_SWIFTNESS);
    @Override
    public List<RegistryEntry<Potion>> getPotions() {
        return POTION_LIST;
    }

    @Override
    public String getGoalName() {
        return "Brew a Potion of Swiftness";
    }

}
