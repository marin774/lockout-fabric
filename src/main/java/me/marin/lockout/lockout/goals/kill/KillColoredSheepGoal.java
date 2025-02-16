package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.interfaces.KillMobGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class KillColoredSheepGoal extends KillMobGoal implements TextureProvider {

    private static final Item ITEM = Items.SHEEP_SPAWN_EGG;

    private final Identifier texture;
    private final String GOAL_NAME;
    private final DyeColor DYE_COLOR;

    public KillColoredSheepGoal(String id, String data) {
        super(id, data);
        texture = Identifier.of(Constants.NAMESPACE, "textures/custom/sheep/kill_" + data + "_sheep.png");
        DYE_COLOR = GoalDataConstants.getDyeColor(data);
        GOAL_NAME = "Kill " + GoalDataConstants.getDyeColorFormatted(DYE_COLOR) + " Sheep";
    }

    @Override
    public EntityType<?> getEntity() {
        return EntityType.SHEEP;
    }

    public DyeColor getDyeColor() {
        return DYE_COLOR;
    }

    @Override
    public String getGoalName() {
        return GOAL_NAME;
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

    @Override
    public Identifier getTextureIdentifier() {
        return texture;
    }

}
