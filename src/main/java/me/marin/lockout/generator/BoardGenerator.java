package me.marin.lockout.generator;

import me.marin.lockout.LocateData;
import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.LockoutTeamServer;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import oshi.util.tuples.Pair;

import java.lang.reflect.Method;
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

    public List<Pair<String, String>> generateBoard() {
        // Lockout.log("Generating a board with " + registeredGoals.size() + " total goals.");
        // Shuffle the list.
        Collections.shuffle(registeredGoals);

        List<Pair<String, String>> goals = new ArrayList<>();
        List<String> goalTypes = new ArrayList<>();

        ListIterator<String> it = registeredGoals.listIterator();
        while (goals.size() < 25 && it.hasNext()) {
            String goal = it.next();

            // Lockout.log("Goal: " + goal);

            if (!GoalGroup.canAdd(goal, goalTypes)) {
                // Lockout.log("Can't add goal " + goal + " due to group limits.");
                continue;
            }

            GoalRequirementsProvider goalRequirements = GoalRegistry.INSTANCE.getGoalGenerator(goal);
            if (goalRequirements != null) {
                if (goalRequirements.isTeamsOnly() && teams.size() != 2) {
                    // Lockout.log("Can't add goal " + goal + " because it's only available with 2 or more teams.");
                    continue;
                }
                if (!goalRequirements.isPartOfRandomPool()) {
                    // Lockout.log("Can't add goal " + goal + " because it's not a part of random goal pool.");
                    continue;
                }
                if (!goalRequirements.isSatisfied(biomes, structures)) {
                    // Lockout.log("Can't add goal " + goal + " due to no biomes/structures found.");
                    continue;
                }
            }

            // Lockout.log("Goal requirements passed for " + goal + "!");

            GoalDataGenerator dataGenerator = GoalRegistry.INSTANCE.getDataGenerator(goal);
            String data = dataGenerator == null ? GoalDataConstants.DATA_NONE : dataGenerator.generateData(new ArrayList<>(attainableDyes));

            // Lockout.log("Added goal " + goal + " (" + data + "). [" + goals.size() + "/25]");

            goals.add(new Pair<>(goal, data));
            goalTypes.add(goal);
        }

        if (goals.size() < 25) {
            // Lockout.log("Board generation failed. Trying to generate a new board.");
            return generateBoard();
        }

        // Lockout.log("Generated goals: " + goals);

        return goals;
    }

}
