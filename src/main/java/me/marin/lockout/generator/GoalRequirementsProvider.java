package me.marin.lockout.generator;

import me.marin.lockout.LocateData;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class GoalRequirementsProvider {

    public static final GoalRequirementsProvider VILLAGE = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return List.of(StructureKeys.VILLAGE_DESERT, StructureKeys.VILLAGE_PLAINS, StructureKeys.VILLAGE_SAVANNA, StructureKeys.VILLAGE_SNOWY, StructureKeys.VILLAGE_TAIGA);
        }
    };
    public static final GoalRequirementsProvider MONUMENT = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return List.of(StructureKeys.MONUMENT);
        }
    };
    public static final GoalRequirementsProvider JUNGLE_BIOMES = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return List.of(BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.JUNGLE, BiomeKeys.SPARSE_JUNGLE);
        }
    };
    public static final GoalRequirementsProvider RABBIT_BIOMES = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return List.of(BiomeKeys.DESERT, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA, BiomeKeys.GROVE, BiomeKeys.SNOWY_SLOPES, BiomeKeys.FLOWER_FOREST,
                    BiomeKeys.TAIGA, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, BiomeKeys.CHERRY_GROVE);
        }
    };
    public static final GoalRequirementsProvider TEAMS_GOAL = new GoalRequirementsProvider() {
        @Override
        public boolean isTeamsSizeOk(int size) {
            return size >= 2;
        }
    };
    public static final GoalRequirementsProvider TO2_ONLY_GOAL = new GoalRequirementsProvider() {
        @Override
        public boolean isTeamsSizeOk(int size) {
            return size == 2;
        }
    };
    public static final GoalRequirementsProvider TO2_ONLY_GOAL_NOT_IN_RANDOM_POOL = new GoalRequirementsProvider() {
        @Override
        public boolean isTeamsSizeOk(int size) {
            return size == 2;
        }
        @Override
        public boolean isPartOfRandomPool() {
            return false;
        }
    };
    public static final GoalRequirementsProvider NOT_IN_RANDOM_POOL = new GoalRequirementsProvider() {
        @Override
        public boolean isPartOfRandomPool() {
            return false;
        }
    };

    /**
     * At least one of these biomes needs to be close to spawn. Keys can be found in {@link BiomeKeys}.
     */
    public List<RegistryKey<Biome>> getRequiredBiomes() {
        return Collections.emptyList();
    }


    /**
     * At least one of these structures needs to be close to spawn. Keys can be found in {@link StructureKeys}.
     */
    public List<RegistryKey<Structure>> getRequiredStructures() {
        return Collections.emptyList();
    }

    public boolean isPartOfRandomPool() {
        return true;
    }

    public boolean isTeamsSizeOk(int teamsSize) {
        return true;
    }


    public final boolean isSatisfied(Map<RegistryKey<Biome>, LocateData> biomes, Map<RegistryKey<Structure>, LocateData> structures) {
        boolean hasRequiredBiome = true;
        if (getRequiredBiomes() != null) {
            for (RegistryKey<Biome> biome : getRequiredBiomes()) {
                if (biomes.get(biome).isInRequiredDistance()) {
                    hasRequiredBiome = true;
                    break;
                } else {
                    hasRequiredBiome = false;
                }
            }
        }


        boolean hasRequiredStructure = true;
        if (getRequiredStructures() != null) {
            for (RegistryKey<Structure> structure : getRequiredStructures()) {
                if (structures.get(structure).isInRequiredDistance()) {
                    hasRequiredStructure = true;
                    break;
                } else {
                    hasRequiredStructure = false;
                }
            }
        }

        return hasRequiredBiome && hasRequiredStructure;
    }

}
