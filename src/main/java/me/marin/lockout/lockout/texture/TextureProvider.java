package me.marin.lockout.lockout.texture;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public interface TextureProvider extends CustomTextureRenderer {

    Identifier getTextureIdentifier();

    @Override
    default boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(RenderLayer::getGuiTextured, getTextureIdentifier(), x, y, 0, 0, 16, 16, 16, 16);
        return true;
    }

}
