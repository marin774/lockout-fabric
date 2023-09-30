package me.marin.lockout.lockout.texture;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.List;

public interface CycleTexturesProvider extends CustomTextureRenderer {

    List<Identifier> getTexturesToDisplay();

    @Override
    default boolean renderTexture(DrawContext context, int x, int y, int tick) {
        int mod = tick % (60 * getTexturesToDisplay().size());
        context.drawTexture(getTexturesToDisplay().get(mod / 60), x, y, 0, 0, 16, 16, 16, 16);
        return true;
    }

}
