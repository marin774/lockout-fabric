package me.marin.lockout.lockout;

import me.marin.lockout.generator.GoalDataGenerator;
import me.marin.lockout.generator.GoalRequirementsProvider;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.util.*;

public class GoalRegistry {

    public static final GoalRegistry INSTANCE = new GoalRegistry();

    private final Map<String, Class<? extends Goal>> registry = new HashMap<>();
    private final Map<String, GoalDataGenerator> goalDataGenerators = new HashMap<>();
    private final Map<String, GoalRequirementsProvider> goalGeneratorProviders = new HashMap<>();

    private GoalRegistry() {}

    public void register(String id, Class<? extends Goal> goalClass) {
        register(id, goalClass, null);
    }
    public void register(String id, Class<? extends Goal> goalClass, GoalRequirementsProvider goalRequirementsProvider) {
        register(id, goalClass, goalRequirementsProvider, null);
    }
    public void register(String id, Class<? extends Goal> goalClass, GoalRequirementsProvider goalRequirementsProvider, GoalDataGenerator goalDataGenerator) {
        registry.put(id, goalClass);
        goalGeneratorProviders.put(id, goalRequirementsProvider);
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

    public GoalRequirementsProvider getGoalGenerator(String id) {
        return goalGeneratorProviders.get(id);
    }

    public List<String> getRegisteredGoals() {
        return new ArrayList<>(registry.keySet());
    }

}
