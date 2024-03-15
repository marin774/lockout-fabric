package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public abstract class GetUniqueAdvancementsGoal extends Goal implements RequiresAmount, Trackable<LockoutTeam, LinkedHashSet<Identifier>>, TextureProvider, HasTooltipInfo {

    private final ItemStack DISPLAY_ITEM_STACK = Items.APPLE.getDefaultStack();

    public GetUniqueAdvancementsGoal(String id, String data) {
        super(id, data);
        DISPLAY_ITEM_STACK.setCount(getAmount());
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    @Override
    public Map<LockoutTeam, LinkedHashSet<Identifier>> getTrackerMap() {
        return LockoutServer.lockout.uniqueAdvancements;
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> tooltip = new ArrayList<>();
        var advancements = getTrackerMap().getOrDefault(team, new LinkedHashSet<>());

        tooltip.add(" ");
        tooltip.add("Advancements: " + advancements.size() + "/" + getAmount());
        tooltip.addAll(HasTooltipInfo.commaSeparatedList(advancements.stream().map(id -> LockoutServer.server.getAdvancementLoader().get(id).getDisplay().getTitle().getString()).toList()));
        tooltip.add(" ");

        return tooltip;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            var advancements = getTrackerMap().getOrDefault(team, new LinkedHashSet<>());
            tooltip.add(team.getColor() + team.getDisplayName() + Formatting.RESET + ": " + advancements.size() + "/" + getAmount());
        }
        tooltip.add(" ");

        return tooltip;
    }

}
