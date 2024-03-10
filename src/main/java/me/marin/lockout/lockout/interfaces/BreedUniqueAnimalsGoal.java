package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class BreedUniqueAnimalsGoal extends Goal implements RequiresAmount, CustomTextureRenderer, HasTooltipInfo {

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/breed/breed_x.png");
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
    public List<String> getTooltip(LockoutTeam team) {
        List<String> lore = new ArrayList<>();
        var animals = LockoutServer.lockout.bredAnimalTypes.getOrDefault(team, new LinkedHashSet<>());

        lore.add(" ");
        lore.add("Animals bred: " + animals.size() + "/" + getAmount());
        lore.addAll(HasTooltipInfo.commaSeparatedList(animals.stream().map(type -> type.getName().getString()).toList()));
        lore.add(" ");

        return lore;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> lore = new ArrayList<>();

        lore.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            var animals = LockoutServer.lockout.bredAnimalTypes.getOrDefault(team, new LinkedHashSet<>());
            lore.add(team.getColor() + team.getDisplayName() + Formatting.RESET + ": " + animals.size() + "/" + getAmount());
        }
        lore.add(" ");

        return lore;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, DISPLAY_ITEM_STACK, x, y);
        return true;
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

}
