package me.marin.lockout.lockout.texture;

import net.minecraft.client.gui.DrawContext;

public interface CustomTextureRenderer {

    boolean renderTexture(DrawContext context, int x, int y, int tick);

}
