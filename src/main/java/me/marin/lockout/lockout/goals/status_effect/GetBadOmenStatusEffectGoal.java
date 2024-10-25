package me.marin.lockout.lockout.goals.status_effect;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.StatusEffectGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GetBadOmenStatusEffectGoal extends StatusEffectGoal implements TextureProvider {

    public GetBadOmenStatusEffectGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Get Bad Omen";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return StatusEffects.BAD_OMEN.value();
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/status_effect/bad_omen.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }
    
}
