package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class BreedFoxGoal extends BreedAnimalGoal implements TextureProvider {

    public BreedFoxGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed Foxes";
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.FOX;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/breed/breed_fox.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
