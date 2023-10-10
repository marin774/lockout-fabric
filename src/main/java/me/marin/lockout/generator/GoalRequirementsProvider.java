package me.marin.lockout.generator;

import me.marin.lockout.LocateData;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.List;
import java.util.Map;

public interface GoalRequirementsProvider {

    GoalRequirementsProvider VILLAGE = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return null;
        }

        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return List.of(StructureKeys.VILLAGE_DESERT, StructureKeys.VILLAGE_PLAINS, StructureKeys.VILLAGE_SAVANNA, StructureKeys.VILLAGE_SNOWY, StructureKeys.VILLAGE_TAIGA);
        }
    };
    GoalRequirementsProvider MONUMENT = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return null;
        }

        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return List.of(StructureKeys.MONUMENT);
        }
    };
    GoalRequirementsProvider JUNGLE_BIOMES = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return List.of(BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.JUNGLE, BiomeKeys.SPARSE_JUNGLE);
        }

        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return null;
        }
    };
    GoalRequirementsProvider RABBIT_BIOMES = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return List.of(BiomeKeys.DESERT, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA, BiomeKeys.GROVE, BiomeKeys.SNOWY_SLOPES, BiomeKeys.FLOWER_FOREST,
                    BiomeKeys.TAIGA, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, BiomeKeys.CHERRY_GROVE);
        }

        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return null;
        }
    };
    GoalRequirementsProvider TEAMS_GOAL = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return null;
        }

        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return null;
        }

        @Override
        public boolean isTeamsSizeOk(int size) {
            return size >= 2;
        }
    };
    GoalRequirementsProvider TO2_ONLY_GOAL = new GoalRequirementsProvider() {
        @Override
        public List<RegistryKey<Biome>> getRequiredBiomes() {
            return null;
        }

        @Override
        public List<RegistryKey<Structure>> getRequiredStructures() {
            return null;
        }

        @Override
        public boolean isTeamsSizeOk(int size) {
            return size == 2;
        }

        @Override
        public boolean isPartOfRandomPool() {
            return false;
        }
    };


    /**
     * Any of these biomes must be present
     * BiomeKeys.BIOME_NAME
     */
    List<RegistryKey<Biome>> getRequiredBiomes();


    /**
     * Any of these structures must be present
     * StructureKeys.STRUCTURE_NAME
     */
    List<RegistryKey<Structure>> getRequiredStructures();




    default boolean isSatisfied(Map<RegistryKey<Biome>, LocateData> biomes, Map<RegistryKey<Structure>, LocateData> structures) {
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

    default boolean isPartOfRandomPool() {
        return true;
    }
    default boolean isTeamsSizeOk(int teamsSize) {
        return true;
    }

}
