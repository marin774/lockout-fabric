package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import me.marin.lockout.lockout.interfaces.KillAllSpecificMobsGoal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class KillAllRaidMobsGoal extends KillAllSpecificMobsGoal implements CustomTextureRenderer {

    private static final ItemStack DISPLAY_ITEM_STACK = Items.VILLAGER_SPAWN_EGG.getDefaultStack();
    private static final List<EntityType<?>> MOBS = List.of(EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.RAVAGER, EntityType.WITCH, EntityType.VEX, EntityType.EVOKER);
    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/status_effect/bad_omen.png");

    static {
        DISPLAY_ITEM_STACK.setCount(MOBS.size());
    }

    public KillAllRaidMobsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill all Raid Mobs";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    @Override
    public List<EntityType<?>> getEntityTypes() {
        return MOBS;
    }

    @Override
    public Map<LockoutTeam, LinkedHashSet<EntityType<?>>> getTrackerMap() {
        return LockoutServer.lockout.killedRaidMobs;
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> lore = new ArrayList<>();
        var raidMobs = getTrackerMap().getOrDefault(team, new LinkedHashSet<>());

        lore.add(" ");
        lore.add("Raid mobs killed: " + raidMobs.size() + "/" + MOBS.size());
        lore.addAll(HasTooltipInfo.commaSeparatedList(raidMobs.stream().map(type -> type.getName().getString()).toList()));
        lore.add(" ");

        return lore;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> lore = new ArrayList<>();

        lore.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            var raidMobs = getTrackerMap().getOrDefault(team, new LinkedHashSet<>());
            lore.add(team.getColor() + team.getDisplayName() + ":" + Formatting.RESET + raidMobs.size() + "/" + MOBS.size());
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

}
