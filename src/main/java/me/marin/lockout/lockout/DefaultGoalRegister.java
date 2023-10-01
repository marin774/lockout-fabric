package me.marin.lockout.lockout;

import me.marin.lockout.GoalGeneratorProvider;
import me.marin.lockout.lockout.goals.advancement.*;
import me.marin.lockout.lockout.goals.advancement.unique.*;
import me.marin.lockout.lockout.goals.biome.*;
import me.marin.lockout.lockout.goals.breed_animals.*;
import me.marin.lockout.lockout.goals.brewing.*;
import me.marin.lockout.lockout.goals.consume.*;
import me.marin.lockout.lockout.goals.consume.unique.*;
import me.marin.lockout.lockout.goals.death.*;
import me.marin.lockout.lockout.goals.experience.*;
import me.marin.lockout.lockout.goals.kill.*;
import me.marin.lockout.lockout.goals.kill.unique.*;
import me.marin.lockout.lockout.goals.mine.*;
import me.marin.lockout.lockout.goals.dimension.*;
import me.marin.lockout.lockout.goals.misc.*;
import me.marin.lockout.lockout.goals.more.HaveMoreXPLevelsGoal;
import me.marin.lockout.lockout.goals.obtain.*;
import me.marin.lockout.lockout.goals.opponent.*;
import me.marin.lockout.lockout.goals.ride.*;
import me.marin.lockout.lockout.goals.status_effect.*;
import me.marin.lockout.lockout.goals.status_effect.unique.*;
import me.marin.lockout.lockout.goals.tame_animal.*;
import me.marin.lockout.lockout.goals.wear_armor.*;
import me.marin.lockout.lockout.goals.workstation.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.List;

public class DefaultGoalRegister {

    public static void registerGoals() {
        GoalRegistry.INSTANCE.register(GoalType.MINE_DIAMOND_ORE, MineDiamondOreGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.MINE_EMERALD_ORE, MineEmeraldOreGoal.class,
                new GoalGeneratorProvider() {
                    // check for: Jagged Peaks, Frozen Peaks, Stony Peaks, Grove, Snowy slopes
                    @Override
                    public List<RegistryKey<Biome>> getRequiredBiomes() {
                        return List.of(BiomeKeys.JAGGED_PEAKS, BiomeKeys.FROZEN_PEAKS, BiomeKeys.STONY_PEAKS, BiomeKeys.GROVE, BiomeKeys.SNOWY_SLOPES);
                    }

                    @Override
                    public List<RegistryKey<Structure>> getRequiredStructures() {
                        return null;
                    }
                });
        GoalRegistry.INSTANCE.register(GoalType.MINE_MOB_SPAWNER, MineMobSpawnerGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.MINE_TURTLE_EGG, MineTurtleEggGoal.class,
                new GoalGeneratorProvider() {
                    @Override
                    public List<RegistryKey<Biome>> getRequiredBiomes() {
                        return List.of(BiomeKeys.BEACH);
                    }

                    @Override
                    public List<RegistryKey<Structure>> getRequiredStructures() {
                        return null;
                    }
                });
        GoalRegistry.INSTANCE.register(GoalType.ENTER_NETHER, EnterNetherGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.ENTER_END, EnterEndGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_ENDER_DRAGON, GetFreeTheEndAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_WOODEN_TOOLS, ObtainWoodenToolsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_STONE_TOOLS, ObtainStoneToolsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_IRON_TOOLS, ObtainIronToolsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_GOLDEN_TOOLS, ObtainGoldenToolsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_DIAMOND_TOOLS, ObtainDiamondToolsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.FILL_ARMOR_STAND, FillArmorStandGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.WEAR_LEATHER_ARMOR, WearLeatherArmorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.WEAR_GOLDEN_ARMOR, WearGoldenArmorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.WEAR_DIAMOND_ARMOR, WearDiamondArmorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.WEAR_IRON_ARMOR, WearIronArmorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.WEAR_CHAIN_ARMOR_PIECE, WearChainArmorPieceGoal.class,
                new GoalGeneratorProvider() {
                    @Override
                    public List<RegistryKey<Biome>> getRequiredBiomes() {
                        return null;
                    }

                    @Override
                    public List<RegistryKey<Structure>> getRequiredStructures() {
                        return List.of(StructureKeys.VILLAGE_DESERT, StructureKeys.VILLAGE_PLAINS, StructureKeys.VILLAGE_SAVANNA, StructureKeys.VILLAGE_SNOWY, StructureKeys.VILLAGE_TAIGA);
                    }
                });
        GoalRegistry.INSTANCE.register(GoalType.WEAR_COLORED_LEATHER_ARMOR_PIECE, WearColoredLeatherPieceGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.TAME_CAT, TameCatGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.TAME_PARROT, TameParrotGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.TAME_HORSE, TameHorseGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.TAME_WOLF, TameWolfGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_4_UNIQUE_ANIMALS, Breed4UniqueAnimalsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_6_UNIQUE_ANIMALS, Breed6UniqueAnimalsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_8_UNIQUE_ANIMALS, Breed8UniqueAnimalsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_CHICKEN, BreedChickenGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_COW, BreedCowsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_HOGLIN, BreedHoglinGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_PIG, BreedPigGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_RABBIT, BreedRabbitGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_FOX, BreedFoxGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_SHEEP, BreedSheepGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_STRIDER, BreedStriderGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREED_GOAT, BreedGoatGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_WITCH, KillWitchGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_ZOMBIE_VILLAGER, KillZombieVillagerGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_STRAY, KillStrayGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_ZOGLIN, KillZoglinGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_SILVERFISH, KillSilverfishGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_GUARDIAN, KillGuardianGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_GHAST, KillGhastGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_SNOW_GOLEM, KillSnowGolemGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_SNOW_GOLEM_IN_NETHER, KillSnowGolemInNetherGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_ELDER_GUARDIAN, KillElderGuardianGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_COLORED_SHEEP, KillColoredSheepGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_7_UNIQUE_HOSTILE_MOBS, Kill7UniqueHostileMobsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_10_UNIQUE_HOSTILE_MOBS, Kill10UniqueHostileMobsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_13_UNIQUE_HOSTILE_MOBS, Kill13UniqueHostileMobsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_15_UNIQUE_HOSTILE_MOBS, Kill15UniqueHostileMobsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_30_UNDEAD_MOBS, Kill30UndeadMobsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_20_ARTHROPOD_MOBS, Kill20ArthropodMobsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_RED_NETHER_BRICK_STAIRS, ObtainRedNetherBrickStairsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_TROPICAL_FISH_BUCKET, ObtainBucketOfTropicalFishGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_BOOKSHELF, ObtainBookshelfGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_MOSSY_STONE_BRICK_WALL, ObtainMossyStoneBrickWallGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_FLOWERING_AZALEA, ObtainFloweringAzaleaGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_SCAFFOLDING, ObtainScaffoldingGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_END_CRYSTAL, ObtainEndCrystalGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_BELL, ObtainBellGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ENCHANT_BOTTLE, ObtainEnchantBottleGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_POWDER_SNOW_BUCKET, ObtainPowderSnowBucketGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_SOUL_LANTERN, ObtainSoulLanternGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ANCIENT_DEBRIS, ObtainAncientDebrisGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ENDER_CHEST, ObtainEnderChestGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_HEART_OF_THE_SEA, ObtainHeartOfTheSeaGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_WITHER_SKELETON_SKULL, ObtainWitherSkeletonSkullGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_END_ROD, ObtainEndRodGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_SPONGE, ObtainSpongeGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_DRAGON_EGG, ObtainDragonEggGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_TNT, ObtainTNTGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_COBWEB, ObtainCobwebGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_MUD_BRICK_WALL, ObtainMudBrickWallGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_DAYLIGHT_DETECTOR, ObtainDaylightDetectorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_REDSTONE_REPEATER, ObtainRedstoneRepeaterGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_REDSTONE_COMPARATOR, ObtainRedstoneComparatorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_OBSERVER, ObtainObserverGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ACTIVATOR_RAIL, ObtainActivatorRailGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_DETECTOR_RAIL, ObtainDetectorRailGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_POWERED_RAIL, ObtainPoweredRailGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_DISPENSER, ObtainDispenserGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_PISTON, ObtainPistonGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ALL_RAW_ORE_BLOCKS, ObtainAllRawOreBlocksGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ALL_HORSE_ARMOR, ObtainAllHorseArmorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ALL_SEEDS, ObtainAllSeedsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_6_UNIQUE_FLOWERS, Obtain6UniqueFlowersGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_COLORED_GLAZED_TERRACOTTA, ObtainColoredGlazedTerracottaGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_64_COLORED_WOOL, Obtain64ColoredWoolGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_64_COLORED_CONCRETE, Obtain64ColoredConcreteGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_WRITTEN_BOOK, ObtainWrittenBookGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.FILL_INVENTORY_UNIQUE_ITEMS, FillInventoryWithUniqueItemsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_THIS_BOAT_HAS_LEGS_ADVANCEMENT, GetThisBoatHasLegsAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.WEAR_CARVED_PUMPKIN_FOR_5_MINUTES, WearCarvedPumpkinFor5MinutesGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_BREWING_STAND, GetLocalBreweryAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREW_HEALING_POTION, BrewHealingPotionGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREW_INVISIBILITY_POTION, BrewInvisibilityPotionGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREW_POISON_POTION, BrewPoisonPotionGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREW_SWIFTNESS_POTION, BrewSwiftnessPotionGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREW_WATER_BREATHING_POTION, BrewWaterBreathingPotionGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREW_WEAKNESS_POTION, BrewWeaknessPotionGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.BREW_LINGERING_POTION, BrewLingeringPotionGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.VISIT_ICE_SPIKES_BIOME, VisitIceSpikesBiomeGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.VISIT_BADLANDS_BIOME, VisitBadlandsBiomeGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.VISIT_MUSHROOM_BIOME, VisitMushroomBiomeGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_5_UNIQUE_FOOD, Eat5UniqueFoodsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_10_UNIQUE_FOOD, Eat10UniqueFoodsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_15_UNIQUE_FOOD, Eat15UniqueFoodsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_20_UNIQUE_FOOD, Eat20UniqueFoodsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_25_UNIQUE_FOOD, Eat25UniqueFoodsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_CHORUS_FRUIT, EatChorusFruitGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_COOKIE, EatCookieGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_GLOW_BERRY, EatGlowBerryGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_POISONOUS_POTATO, EatPoisonousPotatoGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_PUMPKIN_PIE, EatPumpkinPieGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_RABBIT_STEW, EatRabbitStewGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_SUSPICIOUS_STEW, EatSuspiciousStewGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DRINK_HONEY_BOTTLE, DrinkHoneyBottleGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EAT_CAKE, EatCakeGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_4_UNIQUE_SAPLINGS, Obtain4UniqueSaplingsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.TOOT_GOAT_HORN, TootGoatHornGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_ANY_SPYGLASS_ADVANCEMENT, GetAnySpyglassAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_BULLSEYE_ADVANCEMENT, GetBullseyeAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_HOT_TOURIST_DESTINATIONS_ADVANCEMENT, GetHotTouristDestinationsAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_NOT_QUITE_NINE_LIVES_ADVANCEMENT, GetNotQuiteNineLivesAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_OH_SHINY_ADVANCEMENT, GetOhShinyAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_SNIPER_DUEL_ADVANCEMENT, GetSniperDuelAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_WHAT_A_DEAL_ADVANCEMENT, GetWhatADealAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_ABSORPTION_STATUS_EFFECT, GetAbsorptionStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_BAD_OMEN_STATUS_EFFECT, GetBadOmenStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_GLOWING_STATUS_EFFECT, GetGlowingStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_JUMP_BOOST_STATUS_EFFECT, GetJumpBoostStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_LEVITATION_STATUS_EFFECT, GetLevitationStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_MINING_FATIGUE_STATUS_EFFECT, GetMiningFatigueStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_NAUSEA_STATUS_EFFECT, GetNauseaStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_POISON_STATUS_EFFECT, GetPoisonStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_WEAKNESS_STATUS_EFFECT, GetWeaknessStatusEffectGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_ANVIL, DieByAnvilGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_BEE_STING, DieByBeeStingGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_BERRY_BUSH, DieByBerryBushGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_CACTUS, DieByCactusGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_FALLING_OFF_VINE, DieByFallingOffVinesGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_FALLING_STALACTITE, DieByFallingStalactiteGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_FIREWORK, DieByFireworkGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_INTENTIONAL_GAME_DESIGN, DieByIntentionalGameDesignGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_IRON_GOLEM, DieByIronGolemGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_MAGIC, DieByMagicGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.DIE_BY_TNT_MINECART, DieByTNTMinecartGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_A_TERRIBLE_FORTRESS_ADVANCEMENT, GetATerribleFortressAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_THE_CITY_AT_THE_END_OF_THE_GAME_ADVANCEMENT, GetCityAtTheEndOfTheGameAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_EYE_SPY_ADVANCEMENT, GetEyeSpyAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_THOSE_WERE_THE_DAYS_ADVANCEMENT, GetThoseWereTheDaysAdvancementGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.REMOVE_STATUS_EFFECT_USING_MILK, RemoveStatusEffectUsingMilkGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_3_STATUS_EFFECTS_AT_ONCE, Get3StatusEffectsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_4_STATUS_EFFECTS_AT_ONCE, Get4StatusEffectsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_6_STATUS_EFFECTS_AT_ONCE, Get6StatusEffectsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.REACH_EXP_LEVEL_15, ReachXPLevel15Goal.class);
        GoalRegistry.INSTANCE.register(GoalType.REACH_EXP_LEVEL_30, ReachXPLevel30Goal.class);
        GoalRegistry.INSTANCE.register(GoalType.RIDE_HORSE, RideHorseGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.RIDE_MINECART, RideMinecartGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.RIDE_PIG, RidePigGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_STONECUTTER, UseStonecutterGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_ANVIL, UseAnvilGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_ENCHANTING_TABLE, UseEnchantingTableGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_GRINDSTONE, UseGrindstoneGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_LOOM, UseLoomGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_SMITHING_TABLE, UseSmithingTableGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_CAULDRON, UseCauldronGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_COMPOSTER, UseComposterGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_JUKEBOX, UseJukeboxGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.USE_GLOW_INK, UseGlowInkGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.EMPTY_HUNGER_BAR, EmptyHungerBarGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.REACH_HEIGHT_LIMIT, ReachHeightLimitGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.REACH_BEDROCK, ReachBedrockGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_CLOCK, ObtainClockGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_6_UNIQUE_BUCKETS, Obtain6UniqueBucketsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ALL_MINECARTS, ObtainAllMinecartsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ALL_MUSHROOMS, ObtainAllMushroomsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_7_UNIQUE_WORKSTATIONS, Obtain7WorkstationsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_REDSTONE_LAMP, ObtainRedstoneLampGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_SOUL_CAMPFIRE, ObtainSoulCampfireGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_ALL_PUMPKINS, ObtainAllPumpkinsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.ENRAGE_ZOMBIFIED_PIGLIN, AngerZombifiedPiglinGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OBTAIN_BRICK_WALL, ObtainBrickWallGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_10_ADVANCEMENTS, Get10UniqueAdvancementsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_20_ADVANCEMENTS, Get20UniqueAdvancementsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.GET_30_ADVANCEMENTS, Get30UniqueAdvancementsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.WEAR_UNIQUE_COLORED_LEATHER_ARMOR, WearUniqueColoredLeatherArmorGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.KILL_OTHER_PLAYER, KillOtherTeamPlayer.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_OBTAINS_CRAFTING_TABLE, OpponentObtainsCraftingTableGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_OBTAINS_OBSIDIAN, OpponentObtainsObsidianGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_OBTAINS_SEEDS, OpponentObtainsSeedsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_CATCHES_ON_FIRE, OpponentCatchesOnFireGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.TAKE_200_DAMAGE, Take200DamageGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_DIES_3_TIMES, OpponentDies3TimesGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_DIES, OpponentDiesGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_HIT_BY_EGG, OpponentHitByEggGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_HIT_BY_SNOWBALL, OpponentHitBySnowballGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_JUMPS, OpponentJumpsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_TAKES_100_DAMAGE, OpponentTakes100DamageGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_TAKES_FALL_DAMAGE, OpponentTakesFallDamageGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.OPPONENT_TOUCHES_WATER, OpponentTouchesWaterGoal.class);

        GoalRegistry.INSTANCE.register(GoalType.REACH_NETHER_ROOF, ReachNetherRoofGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.HAVE_MORE_XP_LEVELS, HaveMoreXPLevelsGoal.class);
        GoalRegistry.INSTANCE.register(GoalType.FREEZE_TO_DEATH, DieByFreezingGoal.class);


    }

}
