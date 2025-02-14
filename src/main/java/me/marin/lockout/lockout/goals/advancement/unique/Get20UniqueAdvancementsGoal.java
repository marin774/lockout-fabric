package me.marin.lockout.lockout.goals.advancement.unique;

import me.marin.lockout.lockout.interfaces.GetUniqueAdvancementsGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.util.Identifier;

import static me.marin.lockout.Constants.NAMESPACE;

public class Get20UniqueAdvancementsGoal extends GetUniqueAdvancementsGoal implements TextureProvider {

    public Get20UniqueAdvancementsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Get 20 Advancements";
    }

    @Override
    public int getAmount() {
        return 20;
    }

    private static final Identifier TEXTURE = Identifier.of(NAMESPACE, "textures/custom/advancements/20_advancements.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }
    
}
