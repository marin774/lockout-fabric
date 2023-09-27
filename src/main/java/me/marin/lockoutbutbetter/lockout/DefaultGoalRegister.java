package me.marin.lockoutbutbetter.lockout;

import me.marin.lockoutbutbetter.lockout.goals.*;
import me.marin.lockoutbutbetter.lockout.goals.kill.KillEnderDragon;
import me.marin.lockoutbutbetter.lockout.goals.mine.*;
import me.marin.lockoutbutbetter.lockout.goals.dimension.*;
import me.marin.lockoutbutbetter.lockout.goals.obtain.*;

public class DefaultGoalRegister {

    public static void registerGoals() {
        GoalRegistry.INSTANCE.register(GoalType.EAT_STEAK, EatSteakGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.MINE_DIAMOND_ORE, MineDiamondOreGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.MINE_EMERALD_ORE, MineEmeraldOreGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.MINE_MOB_SPAWNER, MineMobSpawnerGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.MINE_TURTLE_EGG, MineTurtleEggGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.ENTER_NETHER, EnterNetherGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.ENTER_END, EnterEndGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_ENDER_DRAGON, KillEnderDragon.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_WOODEN_TOOLS, ObtainWoodenTools.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_STONE_TOOLS, ObtainStoneTools.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_IRON_TOOLS, ObtainIronTools.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_GOLDEN_TOOLS, ObtainGoldenTools.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_DIAMOND_TOOLS, ObtainDiamondTools.class);
    }

}
