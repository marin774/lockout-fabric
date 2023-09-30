package me.marin.lockout.lockout.goals.brewing;

import me.marin.lockout.lockout.interfaces.ObtainPotionItemGoal;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;

import java.util.List;

public class BrewPoisonPotionGoal extends ObtainPotionItemGoal {

    public BrewPoisonPotionGoal(String id, String data) {
        super(id, data);
    }

    private static final List<Potion> POTION_LIST = List.of(Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON);
    @Override
    public List<Potion> getPotions() {
        return POTION_LIST;
    }

    @Override
    public String getGoalName() {
        return "Brew a Potion of Poison";
    }

}
