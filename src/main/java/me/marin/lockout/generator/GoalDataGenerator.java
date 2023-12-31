package me.marin.lockout.generator;

import net.minecraft.util.DyeColor;

import java.util.List;

@FunctionalInterface
public interface GoalDataGenerator {

    String generateData(List<DyeColor> attainableDyes);

}
