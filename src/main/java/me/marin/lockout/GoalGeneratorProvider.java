package me.marin.lockout;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.Map;

public interface GoalGeneratorProvider {

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


    /**
     * Override to generate
     */
    default String generateData(List<DyeColor> attainableDyes, Map<RegistryKey<Structure>, LocateData> structures, Map<RegistryKey<Structure>, LocateData> biomes) {
        return null;
    }

    default boolean canGenerate(List<DyeColor> attainableDyes, Map<RegistryKey<Structure>, LocateData> structures, Map<RegistryKey<Structure>, LocateData> biomes) {
        return true;
    }

    default boolean isPartOfRandomPool() {
        return true;
    }

}
