package me.marin.lockoutbutbetter.lockout;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.util.HashMap;
import java.util.Map;

public class GoalRegistry {

    public static final GoalRegistry INSTANCE = new GoalRegistry();

    private final Map<String, Class<? extends Goal>> registry = new HashMap<>();

    private GoalRegistry() {}

    public void register(String id, Class<? extends Goal> goalClass) {
        registry.put(id, goalClass);
    }

    public Goal newGoal(String id) {
        try {
            return ConstructorUtils.invokeConstructor(registry.get(id), id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
