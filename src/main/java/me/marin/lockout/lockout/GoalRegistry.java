package me.marin.lockout.lockout;

import me.marin.lockout.GoalDataGenerator;
import me.marin.lockout.GoalGeneratorProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.util.*;

public class GoalRegistry {

    public static final GoalRegistry INSTANCE = new GoalRegistry();

    private final Map<String, Class<? extends Goal>> registry = new HashMap<>();
    private final Map<String, GoalDataGenerator> goalDataGenerators = new HashMap<>();
    private final Map<String, GoalGeneratorProvider> goalGeneratorProviders = new HashMap<>();

    public static GoalDataGenerator EMPTY_DATA_GENERATOR = (attainableDyes) -> null;
    public static GoalGeneratorProvider EMPTY_GOAL_PROVIDER = new GoalGeneratorProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return Collections.emptyList();
        }

        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return Collections.emptyList();
        }
    };

    private GoalRegistry() {}

    public void register(String id, Class<? extends Goal> goalClass) {
        register(id, goalClass, EMPTY_GOAL_PROVIDER);
    }
    public void register(String id, Class<? extends Goal> goalClass, GoalGeneratorProvider goalGeneratorProvider) {
        register(id, goalClass, goalGeneratorProvider, EMPTY_DATA_GENERATOR);
    }
    public void register(String id, Class<? extends Goal> goalClass, GoalGeneratorProvider goalGeneratorProvider, GoalDataGenerator goalDataGenerator) {
        registry.put(id, goalClass);
        goalGeneratorProviders.put(id, goalGeneratorProvider);
        goalDataGenerators.put(id, goalDataGenerator);
    }

    public Goal newGoal(String id, String data) {
        try {
            return ConstructorUtils.invokeConstructor(registry.get(id), id, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GoalDataGenerator getDataGenerator(String id) {
        return goalDataGenerators.get(id);
    }

    public GoalGeneratorProvider getGoalGenerator(String id) {
        return goalGeneratorProviders.get(id);
    }

    public Set<String> getRegisteredGoals() {
        return new HashSet<>(registry.keySet());
    }

}
