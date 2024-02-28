package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BreedFrogsGoal extends BreedAnimalGoal implements TextureProvider {

    public BreedFrogsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed Frogs";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.FROG;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/breed/breed_frogs.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
