package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class BreedUniqueAnimalsGoal extends Goal implements RequiresAmount, CustomTextureRenderer, HasTooltipInfo {

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/breed/breed_x.png");
    private final ItemStack DISPLAY_ITEM_STACK = Items.WHEAT.getDefaultStack();

    public BreedUniqueAnimalsGoal(String id, String data) {
        super(id, data);
        DISPLAY_ITEM_STACK.setCount(getAmount());
    }

    @Override
    public String getGoalName() {
        return String.format("Breed %d Unique Animals", getAmount());
    }

    @Override
    public List<String> getTooltip(LockoutTeam team, PlayerEntity player) {
        List<String> tooltip = new ArrayList<>();
        var animals = LockoutServer.lockout.bredAnimalTypes.getOrDefault(team, new LinkedHashSet<>());

        tooltip.add(" ");
        tooltip.add("Animals bred: " + animals.size() + "/" + getAmount());
        tooltip.addAll(HasTooltipInfo.commaSeparatedList(animals.stream().map(type -> type.getName().getString()).toList()));
        tooltip.add(" ");

        return tooltip;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            var animals = LockoutServer.lockout.bredAnimalTypes.getOrDefault(team, new LinkedHashSet<>());
            tooltip.add(team.getColor() + team.getDisplayName() + Formatting.RESET + ": " + animals.size() + "/" + getAmount());
        }
        tooltip.add(" ");

        return tooltip;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, DISPLAY_ITEM_STACK, x, y);
        return true;
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

}
