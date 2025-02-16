package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import me.marin.lockout.lockout.interfaces.RequiresAmount;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import me.marin.lockout.lockout.texture.TextureProvider;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Kill100MobsGoal extends Goal implements TextureProvider, CustomTextureRenderer, HasTooltipInfo, RequiresAmount {

    private final static ItemStack DISPLAY_ITEM_STACK = Items.RED_DYE.getDefaultStack();
    static {
        DISPLAY_ITEM_STACK.setCount(64);
    }
    public Kill100MobsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill " + getAmount() + " mobs";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/kill/kill_100_mobs.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, DISPLAY_ITEM_STACK, x, y, String.valueOf(getAmount()));
        return true;
    }

    @Override
    public List<String> getTooltip(LockoutTeam team, PlayerEntity player) {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        tooltip.add("Mobs killed: " + LockoutServer.lockout.mobsKilled.getOrDefault(team, 0) + "/" + getAmount());
        tooltip.add(" ");

        return tooltip;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            tooltip.add(team.getColor() + team.getDisplayName() + Formatting.RESET + ": " + LockoutServer.lockout.mobsKilled.getOrDefault(team, 0) + "/" + getAmount());
        }
        tooltip.add(" ");

        return tooltip;
    }

    @Override
    public int getAmount() {
        return 100;
    }
}
