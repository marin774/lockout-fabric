package me.marin.lockout.lockout.goals.opponent;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class OpponentDies3TimesGoal extends Goal implements CustomTextureRenderer {

    private final static ItemStack DISPLAY_ITEM_STACK = Items.PLAYER_HEAD.getDefaultStack();
    static {
        DISPLAY_ITEM_STACK.setCount(3);
    }
    public OpponentDies3TimesGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Opponent dies 3 times";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/opponent/no_death.png");

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, DISPLAY_ITEM_STACK, x, y);
        return true;
    }

}