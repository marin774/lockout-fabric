package me.marin.lockout.lockout.goals.opponent;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.OpponentObtainsItemGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class OpponentObtainsObsidianGoal extends OpponentObtainsItemGoal implements TextureProvider {

    public OpponentObtainsObsidianGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getMessage(PlayerEntity player) {
        return player.getDisplayName().getString() + " obtained Obsidian.";
    }

    @Override
    public String getGoalName() {
        return "Opponent obtains Obsidian";
    }

    private static final List<Item> ITEMS = List.of(Items.OBSIDIAN);
    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/opponent/no_obsidian.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        return TextureProvider.super.renderTexture(context, x, y, tick);
    }

}
