package me.marin.lockout.generator;

import java.util.ArrayList;
import java.util.List;

import static me.marin.lockout.lockout.GoalType.*;

public class GoalGroup {

    private final static List<GoalGroup> GOAL_GROUPS = new ArrayList<>();

    public static final GoalGroup END = new GoalGroup(List.of(
            ENTER_END, EAT_CHORUS_FRUIT, OBTAIN_DRAGON_EGG, OBTAIN_END_ROD, GET_LEVITATION_STATUS_EFFECT, GET_THE_CITY_AT_THE_END_OF_THE_GAME_ADVANCEMENT, BREW_LINGERING_POTION
    ), 2);
    public static final GoalGroup END_STRUCTURE = new GoalGroup(List.of(
            GET_LEVITATION_STATUS_EFFECT, GET_THE_CITY_AT_THE_END_OF_THE_GAME_ADVANCEMENT
    ), 1);
    public static final GoalGroup TOOLS = new GoalGroup(List.of(
            OBTAIN_WOODEN_TOOLS, OBTAIN_STONE_TOOLS, OBTAIN_IRON_TOOLS,OBTAIN_GOLDEN_TOOLS, OBTAIN_DIAMOND_TOOLS
    ), 2);
    public static final GoalGroup DIAMOND = new GoalGroup(List.of(
            OBTAIN_DIAMOND_TOOLS, WEAR_DIAMOND_ARMOR
    ), 1);
    public static final GoalGroup ARMOR = new GoalGroup(List.of(
            WEAR_LEATHER_ARMOR, WEAR_IRON_ARMOR, WEAR_GOLDEN_ARMOR, WEAR_DIAMOND_ARMOR
    ), 1);
    public static final GoalGroup ARMOR_SPECIAL = new GoalGroup(List.of(
            WEAR_COLORED_LEATHER_ARMOR_PIECE, WEAR_UNIQUE_COLORED_LEATHER_ARMOR, WEAR_CARVED_PUMPKIN_FOR_5_MINUTES, WEAR_CHAIN_ARMOR_PIECE, FILL_ARMOR_STAND
    ), 2);
    public static final GoalGroup KILL_MOB = new GoalGroup(List.of(
            KILL_COLORED_SHEEP, KILL_ELDER_GUARDIAN, KILL_GHAST, KILL_SNOW_GOLEM, KILL_SNOW_GOLEM_IN_NETHER, KILL_SILVERFISH, KILL_STRAY, KILL_WITCH, KILL_ZOMBIE_VILLAGER, KILL_ZOGLIN, KILL_BAT
    ), 1);
    public static final GoalGroup SNOWMAN = new GoalGroup(List.of(
            KILL_SNOW_GOLEM, KILL_SNOW_GOLEM_IN_NETHER
    ), 1);
    public static final GoalGroup KILL_UNIQUE_HOSTILES = new GoalGroup(List.of(
            KILL_7_UNIQUE_HOSTILE_MOBS, KILL_10_UNIQUE_HOSTILE_MOBS, KILL_13_UNIQUE_HOSTILE_MOBS, KILL_15_UNIQUE_HOSTILE_MOBS
    ), 2);
    public static final GoalGroup KILL_HOSTILES_OF_TYPE = new GoalGroup(List.of(
            KILL_20_ARTHROPOD_MOBS, KILL_30_UNDEAD_MOBS
    ), 1);
    public static final GoalGroup FORTRESS = new GoalGroup(List.of(
            BREW_HEALING_POTION, BREW_INVISIBILITY_POTION, BREW_LINGERING_POTION, BREW_POISON_POTION, BREW_SWIFTNESS_POTION, BREW_WATER_BREATHING_POTION, BREW_WEAKNESS_POTION,
            PLACE_END_CRYSTAL, OBTAIN_ENDER_CHEST, USE_BREWING_STAND, OBTAIN_ALL_HORSE_ARMOR
    ), 3);
    public static final GoalGroup BREW_POTION = new GoalGroup(List.of(
            BREW_HEALING_POTION, BREW_INVISIBILITY_POTION, BREW_LINGERING_POTION, BREW_POISON_POTION, BREW_SWIFTNESS_POTION, BREW_WATER_BREATHING_POTION, BREW_WEAKNESS_POTION
    ), 1);
    public static final GoalGroup HORSE = new GoalGroup(List.of(
            TAME_HORSE, RIDE_HORSE, OBTAIN_ALL_HORSE_ARMOR
    ), 1);
    public static final GoalGroup BREED = new GoalGroup(List.of(
            BREED_CHICKEN, BREED_COW, BREED_FOX, BREED_GOAT, BREED_HOGLIN, BREED_PIG, BREED_RABBIT, BREED_SHEEP, BREED_STRIDER, BREED_FROGS
    ), 2);
    public static final GoalGroup BREED_X_UNIQUE = new GoalGroup(List.of(
            BREED_4_UNIQUE_ANIMALS, BREED_6_UNIQUE_ANIMALS, BREED_8_UNIQUE_ANIMALS
    ), 2);
    public static final GoalGroup RIDE = new GoalGroup(List.of(
            RIDE_HORSE, RIDE_MINECART, RIDE_PIG, GET_THIS_BOAT_HAS_LEGS_ADVANCEMENT
    ), 2);
    public static final GoalGroup EAT_FOOD = new GoalGroup(List.of(
            EAT_CAKE, EAT_CHORUS_FRUIT, EAT_COOKIE, EAT_GLOW_BERRY, EAT_POISONOUS_POTATO, EAT_PUMPKIN_PIE, EAT_RABBIT_STEW, EAT_SUSPICIOUS_STEW
    ), 3);
    public static final GoalGroup EAT_X_UNIQUE_FOOD = new GoalGroup(List.of(
            EAT_5_UNIQUE_FOOD, EAT_10_UNIQUE_FOOD, EAT_15_UNIQUE_FOOD, EAT_20_UNIQUE_FOOD, EAT_25_UNIQUE_FOOD
    ), 2);
    public static final GoalGroup EFFECT = new GoalGroup(List.of(
            GET_ABSORPTION_STATUS_EFFECT, GET_BAD_OMEN_STATUS_EFFECT, GET_GLOWING_STATUS_EFFECT, GET_JUMP_BOOST_STATUS_EFFECT, GET_LEVITATION_STATUS_EFFECT,
            GET_MINING_FATIGUE_STATUS_EFFECT, GET_NAUSEA_STATUS_EFFECT, GET_POISON_STATUS_EFFECT, GET_WEAKNESS_STATUS_EFFECT
    ), 4);
    public static final GoalGroup EFFECT_X = new GoalGroup(List.of(
            GET_3_STATUS_EFFECTS_AT_ONCE, GET_4_STATUS_EFFECTS_AT_ONCE, GET_6_STATUS_EFFECTS_AT_ONCE
    ), 1);
    public static final GoalGroup DEATH_DAMAGE = new GoalGroup(List.of(
            DIE_BY_ANVIL, DIE_BY_BEE_STING, DIE_BY_BERRY_BUSH, DIE_BY_CACTUS, DIE_BY_FALLING_OFF_VINE, DIE_BY_FALLING_STALACTITE, DIE_BY_FIREWORK,
            DIE_BY_INTENTIONAL_GAME_DESIGN, DIE_BY_IRON_GOLEM, DIE_BY_MAGIC, DIE_BY_TNT_MINECART, OPPONENT_DIES, OPPONENT_DIES_3_TIMES,
            DEAL_400_DAMAGE, OPPONENT_TAKES_100_DAMAGE, TAKE_200_DAMAGE
    ), 3);
    public static final GoalGroup BIOME = new GoalGroup(List.of(
            VISIT_BADLANDS_BIOME, VISIT_ICE_SPIKES_BIOME, VISIT_MUSHROOM_BIOME, GET_HOT_TOURIST_DESTINATIONS_ADVANCEMENT
    ), 1);
    public static final GoalGroup OPPONENT_HARD = new GoalGroup(List.of(
            OPPONENT_OBTAINS_CRAFTING_TABLE, OPPONENT_JUMPS, OPPONENT_TOUCHES_WATER
    ), 1);
    public static final GoalGroup OPPONENT_TAKES_DAMAGE = new GoalGroup(List.of(
            OPPONENT_TAKES_100_DAMAGE, OPPONENT_TAKES_FALL_DAMAGE, OPPONENT_CATCHES_ON_FIRE
    ), 1);
    public static final GoalGroup OPPONENT_GOALS = new GoalGroup(List.of(
            OPPONENT_TAKES_100_DAMAGE, OPPONENT_TAKES_FALL_DAMAGE, OPPONENT_CATCHES_ON_FIRE,
            OPPONENT_OBTAINS_CRAFTING_TABLE, OPPONENT_JUMPS, OPPONENT_TOUCHES_WATER,
            OPPONENT_HIT_BY_EGG, OPPONENT_HIT_BY_SNOWBALL, OPPONENT_DIES, OPPONENT_DIES_3_TIMES
    ), 3);
    public static final GoalGroup OPPONENT_DEATH_DAMAGE = new GoalGroup(List.of(
            OPPONENT_DIES, OPPONENT_DIES_3_TIMES, OPPONENT_TAKES_100_DAMAGE
    ), 1);
    public static final GoalGroup OPPONENT_HIT_BY = new GoalGroup(List.of(
            OPPONENT_HIT_BY_EGG, OPPONENT_HIT_BY_SNOWBALL
    ), 1);
    public static final GoalGroup DAMAGE = new GoalGroup(List.of(
            DEAL_400_DAMAGE, OPPONENT_TAKES_100_DAMAGE, TAKE_200_DAMAGE
    ), 1);
    public static final GoalGroup ADVANCEMENT = new GoalGroup(List.of(
            GET_10_ADVANCEMENTS, GET_20_ADVANCEMENTS, GET_30_ADVANCEMENTS,
            GET_A_TERRIBLE_FORTRESS_ADVANCEMENT, GET_ANY_SPYGLASS_ADVANCEMENT, GET_BULLSEYE_ADVANCEMENT,
            GET_EYE_SPY_ADVANCEMENT, GET_HOT_TOURIST_DESTINATIONS_ADVANCEMENT, GET_NOT_QUITE_NINE_LIVES_ADVANCEMENT,
            GET_OH_SHINY_ADVANCEMENT, GET_SNIPER_DUEL_ADVANCEMENT, GET_THE_CITY_AT_THE_END_OF_THE_GAME_ADVANCEMENT,
            GET_THIS_BOAT_HAS_LEGS_ADVANCEMENT, GET_THOSE_WERE_THE_DAYS_ADVANCEMENT, GET_WHAT_A_DEAL_ADVANCEMENT,
            GET_HIRED_HELP_ADVANCEMENT, GET_WAX_ON_ADVANCEMENT, GET_WAX_OFF_ADVANCEMENT
    ), 4);
    public static final GoalGroup X_ADVANCEMENTS = new GoalGroup(List.of(
            GET_10_ADVANCEMENTS, GET_20_ADVANCEMENTS, GET_30_ADVANCEMENTS
    ), 2);
    public static final GoalGroup VILLAGE = new GoalGroup(List.of(
            EAT_PUMPKIN_PIE, EAT_COOKIE, GET_WHAT_A_DEAL_ADVANCEMENT, OBTAIN_BELL, DIE_BY_IRON_GOLEM, EAT_RABBIT_STEW,
            WEAR_CHAIN_ARMOR_PIECE
    ), 3);
    public static final GoalGroup MONUMENT = new GoalGroup(List.of(
            GET_MINING_FATIGUE_STATUS_EFFECT, KILL_ELDER_GUARDIAN, KILL_GUARDIAN, OBTAIN_SPONGE, DIE_BY_MAGIC
    ), 1);
    public static final GoalGroup REDSTONE = new GoalGroup(List.of(
            OBTAIN_REDSTONE_COMPARATOR, OBTAIN_REDSTONE_LAMP, OBTAIN_CLOCK,
            OBTAIN_REDSTONE_REPEATER, OBTAIN_PISTON, OBTAIN_ACTIVATOR_RAIL,
            OBTAIN_DETECTOR_RAIL, OBTAIN_POWERED_RAIL, OBTAIN_DISPENSER,
            OBTAIN_OBSERVER
    ), 3);
    public static final GoalGroup TNT = new GoalGroup(List.of(
            OBTAIN_ALL_MINECARTS, OBTAIN_TNT, DIE_BY_TNT_MINECART
    ), 1);
    public static final GoalGroup STRIDER = new GoalGroup(List.of(
            BREED_STRIDER, GET_THIS_BOAT_HAS_LEGS_ADVANCEMENT
    ), 1);
    public static final GoalGroup SHEEP = new GoalGroup(List.of(
            BREED_SHEEP, KILL_COLORED_SHEEP
    ), 1);
    public static final GoalGroup OBTAIN_ALL = new GoalGroup(List.of(
           OBTAIN_ALL_HORSE_ARMOR, OBTAIN_ALL_MINECARTS, OBTAIN_ALL_PUMPKINS, OBTAIN_ALL_MUSHROOMS, OBTAIN_ALL_RAW_ORE_BLOCKS, OBTAIN_4_UNIQUE_SEEDS
    ), 2);
    public static final GoalGroup OBTAIN_SEEDS = new GoalGroup(List.of(
            OBTAIN_4_UNIQUE_SEEDS, OPPONENT_OBTAINS_SEEDS
    ), 1);
    public static final GoalGroup HONEY = new GoalGroup(List.of(
            DRINK_HONEY_BOTTLE, GET_WAX_ON_ADVANCEMENT, GET_WAX_OFF_ADVANCEMENT
    ), 1);
    public static final GoalGroup IRON_HEAVY = new GoalGroup(List.of(
            USE_ANVIL, DIE_BY_ANVIL, OBTAIN_ALL_MINECARTS, GET_HIRED_HELP_ADVANCEMENT
    ), 1);
    public static final GoalGroup LEATHER_HEAVY = new GoalGroup(List.of(
            WEAR_LEATHER_ARMOR, WEAR_UNIQUE_COLORED_LEATHER_ARMOR
    ), 1);
    public static final GoalGroup FIREWORKS = new GoalGroup(List.of(
            DIE_BY_FIREWORK, SHOOT_FIREWORK_FROM_CROSSBOW
    ), 1);

    static {
        KILL_UNIQUE_HOSTILES.requirePredecessor.add(KILL_13_UNIQUE_HOSTILE_MOBS);
        KILL_UNIQUE_HOSTILES.requirePredecessor.add(KILL_15_UNIQUE_HOSTILE_MOBS);
        BREED_X_UNIQUE.requirePredecessor.add(BREED_8_UNIQUE_ANIMALS);
        EAT_X_UNIQUE_FOOD.requirePredecessor.add(EAT_20_UNIQUE_FOOD);
        EAT_X_UNIQUE_FOOD.requirePredecessor.add(EAT_25_UNIQUE_FOOD);
        X_ADVANCEMENTS.requirePredecessor.add(GET_30_ADVANCEMENTS);
    }

    static {
        GOAL_GROUPS.add(END);
        GOAL_GROUPS.add(END_STRUCTURE);
        GOAL_GROUPS.add(TOOLS);
        GOAL_GROUPS.add(DIAMOND);
        GOAL_GROUPS.add(ARMOR);
        GOAL_GROUPS.add(ARMOR_SPECIAL);
        GOAL_GROUPS.add(KILL_MOB);
        GOAL_GROUPS.add(SNOWMAN);
        GOAL_GROUPS.add(KILL_UNIQUE_HOSTILES);
        GOAL_GROUPS.add(KILL_HOSTILES_OF_TYPE);
        GOAL_GROUPS.add(FORTRESS);
        GOAL_GROUPS.add(BREW_POTION);
        GOAL_GROUPS.add(HORSE);
        GOAL_GROUPS.add(BREED);
        GOAL_GROUPS.add(BREED_X_UNIQUE);
        GOAL_GROUPS.add(RIDE);
        GOAL_GROUPS.add(EAT_FOOD);
        GOAL_GROUPS.add(EAT_X_UNIQUE_FOOD);
        GOAL_GROUPS.add(EFFECT);
        GOAL_GROUPS.add(EFFECT_X);
        GOAL_GROUPS.add(DEATH_DAMAGE);
        GOAL_GROUPS.add(BIOME);
        GOAL_GROUPS.add(OPPONENT_HARD);
        GOAL_GROUPS.add(OPPONENT_TAKES_DAMAGE);
        GOAL_GROUPS.add(OPPONENT_GOALS);
        GOAL_GROUPS.add(OPPONENT_DEATH_DAMAGE);
        GOAL_GROUPS.add(OPPONENT_HIT_BY);
        GOAL_GROUPS.add(DAMAGE);
        GOAL_GROUPS.add(ADVANCEMENT);
        GOAL_GROUPS.add(X_ADVANCEMENTS);
        GOAL_GROUPS.add(VILLAGE);
        GOAL_GROUPS.add(MONUMENT);
        GOAL_GROUPS.add(REDSTONE);
        GOAL_GROUPS.add(TNT);
        GOAL_GROUPS.add(STRIDER);
        GOAL_GROUPS.add(SHEEP);
        GOAL_GROUPS.add(OBTAIN_ALL);
        GOAL_GROUPS.add(OBTAIN_SEEDS);
        GOAL_GROUPS.add(HONEY);
        GOAL_GROUPS.add(IRON_HEAVY);
        GOAL_GROUPS.add(LEATHER_HEAVY);
        GOAL_GROUPS.add(FIREWORKS);
    }


    private final List<String> goals;
    private final int limit;
    private final List<String> requirePredecessor = new ArrayList<>();

    private GoalGroup(List<String> goals, int limit) {
        this.goals = goals;
        this.limit = limit;
    }

    public List<String> getGoals() {
        return goals;
    }

    public int getLimit() {
        return limit;
    }

    private int countMatches(List<String> boardGoals) {
        int count = 0;
        for (String goal : boardGoals) {
            if (goals.contains(goal)) {
                count++;
            }
        }
        return count;
    }

    private static List<GoalGroup> findGroups(String goalType) {
        List<GoalGroup> found = new ArrayList<>();
        for (GoalGroup group : GOAL_GROUPS) {
            if (group.goals.contains(goalType)) {
                found.add(group);
            }
        }

        return found;
    }

    public static boolean canAdd(String goalType, List<String> board) {
        List<GoalGroup> groups = findGroups(goalType);

        for (GoalGroup group : groups) {
            if (group.countMatches(board) + 1 > group.getLimit()) {
                return false;
            }
            if (group.requirePredecessor.contains(goalType)) {
                if (!hasPredecessorInGroup(goalType, board, group)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean hasPredecessorInGroup(String goalType, List<String> board, GoalGroup group) {
        int max = group.goals.indexOf(goalType);
        if (max < 0) return false;
        for (int i = 0; i < max; i++) {
            String goal = group.goals.get(i);
            if (board.contains(goal)) {
                // Lockout.log(goalType + " has a predecessor " + goal + ".");
                return true;
            }
        }
        return false;
    }

}
