package me.marin.lockout.generator;

import me.marin.lockout.LocateData;
import me.marin.lockout.LockoutTeamServer;
import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import oshi.util.tuples.Pair;

import java.util.*;

public class BoardGenerator {

    private final List<String> registeredGoals;
    private final List<LockoutTeamServer> teams;
    private final List<DyeColor> attainableDyes;
    private final Map<RegistryKey<Biome>, LocateData> biomes;
    private final Map<RegistryKey<Structure>, LocateData> structures;

    public BoardGenerator(List<String> registeredGoals, List<LockoutTeamServer> teams, List<DyeColor> attainableDyes, Map<RegistryKey<Biome>, LocateData> biomes, Map<RegistryKey<Structure>, LocateData> structures) {
        this.registeredGoals = registeredGoals;
        this.teams = teams;
        this.attainableDyes = attainableDyes;
        this.biomes = biomes;
        this.structures = structures;
    }

    public LockoutBoard generateBoard(int size) {
        Collections.shuffle(registeredGoals);

        List<Pair<String, String>> goals = new ArrayList<>();
        List<String> goalTypes = new ArrayList<>();

        ListIterator<String> it = registeredGoals.listIterator();
        while (goals.size() < size * size && it.hasNext()) {
            String goal = it.next();

            if (!GoalGroup.canAdd(goal, goalTypes)) {
                continue;
            }

            GoalRequirements goalRequirements = GoalRegistry.INSTANCE.getGoalGenerator(goal);
            if (goalRequirements != null) {
                if (!goalRequirements.isTeamsSizeOk(teams.size())) {
                    continue;
                }
                if (!goalRequirements.isPartOfRandomPool()) {
                    continue;
                }
                if (!goalRequirements.isSatisfied(biomes, structures)) {
                    continue;
                }
            }

            GoalDataGenerator dataGenerator = GoalRegistry.INSTANCE.getDataGenerator(goal);
            String data = dataGenerator == null ? GoalDataConstants.DATA_NONE : dataGenerator.generateData(attainableDyes);

            goals.add(new Pair<>(goal, data));
            goalTypes.add(goal);
        }

        if (goals.size() < size * size) {
            return generateBoard(size);
        }

        // Shuffle the board again. Some goals will always be after some other goals (GoalGroup#requirePredecessor),
        // and shuffle fixes this.
        Collections.shuffle(goals);

        return new LockoutBoard(goals);
    }

}
