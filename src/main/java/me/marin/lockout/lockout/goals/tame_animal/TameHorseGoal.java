package me.marin.lockout.lockout.goals.tame_animal;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.TameAnimalGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class TameHorseGoal extends TameAnimalGoal implements TextureProvider {

    public TameHorseGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Tame a Horse";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/tame/tame_horse.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.HORSE;
    }
}
