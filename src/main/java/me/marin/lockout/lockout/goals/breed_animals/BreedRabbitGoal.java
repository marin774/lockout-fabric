package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class BreedRabbitGoal extends BreedAnimalGoal implements TextureProvider {

    public BreedRabbitGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed Rabbits";
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.RABBIT;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/breed/breed_rabbit.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
