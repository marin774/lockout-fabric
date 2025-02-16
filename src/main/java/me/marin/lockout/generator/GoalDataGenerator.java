package me.marin.lockout.generator;

import lombok.Getter;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GoalDataGenerator {

    public static final List<DyeColor> ALL_DYES = List.of(
            DyeColor.BLACK, DyeColor.WHITE, DyeColor.GRAY, DyeColor.LIGHT_GRAY,
            DyeColor.BLUE, DyeColor.LIGHT_BLUE, DyeColor.ORANGE, DyeColor.RED,
            DyeColor.YELLOW, DyeColor.MAGENTA, DyeColor.PINK, DyeColor.PURPLE,
            DyeColor.GREEN, DyeColor.LIME, DyeColor.CYAN, DyeColor.BROWN
    );

    private final List<Generator<?>> generators = new ArrayList<>();

    private GoalDataGenerator() {}

    public static GoalDataGenerator builder() {
        return new GoalDataGenerator();
    }

    public GoalDataGenerator withDye(DyeGenerator dyeGenerator) {
        generators.add(dyeGenerator);
        return this;
    }

    public GoalDataGenerator withLeatherArmorPiece(LeatherArmorPieceGenerator leatherArmorPieceGenerator) {
        generators.add(leatherArmorPieceGenerator);
        return this;
    }

    public String generateData(List<DyeColor> attainableDyes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < generators.size(); i++) {
            if (i > 0) sb.append(GoalDataConstants.DATA_SEPARATOR);
            Generator<?> generator = generators.get(i);
            // TODO: smarter way of doing this
            if (generator instanceof DyeGenerator dyeGenerator) {
                sb.append(dyeGenerator.generateData(new ArrayList<>(attainableDyes)));
            } else if (generator instanceof LeatherArmorPieceGenerator leatherArmorPieceGenerator) {
                sb.append(leatherArmorPieceGenerator.generateData(new ArrayList<>(GoalDataConstants.DATA_LEATHER_ARMOR)));
            }
        }
        return sb.toString();
    }


    @FunctionalInterface
    public interface Generator<D> {
        default String getGeneratorName() {
            throw new UnsupportedOperationException("Generator#getGeneratorName was not implemented.");
        }
        default boolean verify(String s) {
            throw new UnsupportedOperationException("Generator#verify was not implemented.");
        }
        String generateData(D data);
    }

    @FunctionalInterface
    public interface DyeGenerator extends Generator<List<DyeColor>> {
        default String getGeneratorName() {
            return "Dye Color";
        }
        default boolean verify(String s) {
            return GoalDataConstants.getDyeColor(s) != null;
        }
    }

    @FunctionalInterface
    public interface LeatherArmorPieceGenerator extends Generator<List<String>> {
        default String getGeneratorName() {
            return "Leather Armor Piece";
        }
        default boolean verify(String s) {
            return GoalDataConstants.DATA_LEATHER_ARMOR.contains(s);
        }
    }

}
