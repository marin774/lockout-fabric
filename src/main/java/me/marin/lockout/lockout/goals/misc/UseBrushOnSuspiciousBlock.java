package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class UseBrushOnSuspiciousBlock extends Goal implements CustomTextureRenderer {

    public UseBrushOnSuspiciousBlock(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Use Brush on Suspicious Sand/Gravel";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return Items.BRUSH.getDefaultStack();
    }

    private static final List<ItemStack> SUSPICIOUS_BLOCKS = List.of(Items.SUSPICIOUS_GRAVEL.getDefaultStack(), Items.SUSPICIOUS_SAND.getDefaultStack());
    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/brush_overlay.png");
    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        int mod = tick % (60 * SUSPICIOUS_BLOCKS.size());
        context.drawItem(SUSPICIOUS_BLOCKS.get(mod / 60), x, y);
        context.drawTexture(RenderLayer::getGuiTexturedOverlay, TEXTURE, x, y, 0,0, 16, 16, 16, 16);
        return true;
    }

}
