package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FillArmorStandGoal extends Goal {

    private static final Item ITEM = Items.ARMOR_STAND;
    // private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/armor_stand.png");

    public FillArmorStandGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Fill an Armor Stand";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

}
