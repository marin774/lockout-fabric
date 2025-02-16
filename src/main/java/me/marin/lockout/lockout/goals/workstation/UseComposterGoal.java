package me.marin.lockout.lockout.goals.workstation;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class UseComposterGoal extends Goal implements TextureProvider {

    public UseComposterGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Fill Composter and get Bone Meal";
    }

    private static final ItemStack ITEM_STACK = Items.COMPOSTER.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/filled_composter.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
