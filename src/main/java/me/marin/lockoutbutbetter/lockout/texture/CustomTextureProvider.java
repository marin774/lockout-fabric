package me.marin.lockoutbutbetter.lockout.texture;

import net.minecraft.client.gui.DrawContext;

public interface CustomTextureProvider {

    void renderTexture(DrawContext context, int x, int y, int tick);

}
