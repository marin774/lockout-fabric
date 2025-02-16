package me.marin.lockout.lockout.texture;

import me.marin.lockout.lockout.Goal;
import net.minecraft.client.gui.DrawContext;

public interface CustomTextureRenderer {

    /**
     * Renders/displays any custom texture. This method is called every client tick, meaning that the texture can change.
     * @param tick Number of client ticks passed. This number increases every client tick.
     * @return Whether the texture was rendered successfully. If this method fails to render (and false is returned),
     *         {@link Goal#getTextureItemStack} will be rendered instead.
     */
    boolean renderTexture(DrawContext context, int x, int y, int tick);

}
