package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ReachBedrockGoal extends Goal implements CustomTextureRenderer {

    private static final ItemStack ITEM_STACK = Items.BEDROCK.getDefaultStack();
    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/down_arrow.png");

    public ReachBedrockGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Reach Bedrock";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawItem(ITEM_STACK, x, y);
        context.drawTexture(RenderLayer::getGuiTexturedOverlay, TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        return true;
    }

}