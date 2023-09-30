package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.Constants;
import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public abstract class EatUniqueFoodsGoal extends Goal implements RequiresAmount, Trackable<PlayerEntity, Set<FoodComponent>>, CustomTextureRenderer {

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/eat_unique.png");
    private final ItemStack DISPLAY_ITEM_STACK = Items.APPLE.getDefaultStack();

    public EatUniqueFoodsGoal(String id, String data) {
        super(id, data);
        DISPLAY_ITEM_STACK.setCount(getAmount());
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, DISPLAY_ITEM_STACK, x, y);
        return true;
    }

    @Override
    public Map<PlayerEntity, Set<FoodComponent>> getTrackerMap() {
        return Lockout.getInstance().foodTypesEaten;
    }

}
