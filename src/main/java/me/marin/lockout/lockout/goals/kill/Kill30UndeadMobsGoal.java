package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.interfaces.KillSpecificMobsGoal;
import me.marin.lockout.lockout.texture.CycleTexturesProvider;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Kill30UndeadMobsGoal extends KillSpecificMobsGoal implements CycleTexturesProvider {

    private static final ItemStack ITEM_STACK = Items.WOODEN_SWORD.getDefaultStack();
    static {
        ITEM_STACK.setCount(30);
    }
    private static final List<Identifier> TEXTURES = List.of(
            Identifier.of(Constants.NAMESPACE, "textures/custom/undead/kill_zombie.png"),
            Identifier.of(Constants.NAMESPACE, "textures/custom/undead/kill_wither_skeleton.png"),
            Identifier.of(Constants.NAMESPACE, "textures/custom/undead/kill_zombie_villager.png"),
            Identifier.of(Constants.NAMESPACE, "textures/custom/undead/kill_drowned.png"),
            Identifier.of(Constants.NAMESPACE, "textures/custom/undead/kill_husk.png"),
            Identifier.of(Constants.NAMESPACE, "textures/custom/undead/kill_stray.png"),
            Identifier.of(Constants.NAMESPACE, "textures/custom/undead/kill_zoglin.png")
    );
    // undead: drowned, husk, phantom, skeleton, skeletonhorse, stray, wither, wither skeleton, zoglin, zombie, zombie horse, zombie villager, zombiefied piglin, bogged
    private static final List<EntityType<?>> UNDEAD_MOBS = List.of(
            EntityType.DROWNED,
            EntityType.HUSK,
            EntityType.PHANTOM,
            EntityType.SKELETON,
            EntityType.SKELETON_HORSE,
            EntityType.STRAY,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.ZOGLIN,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.BOGGED
    );
    public Kill30UndeadMobsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill 30 Undead Mobs";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    @Override
    public List<EntityType<?>> getEntityTypes() {
        return UNDEAD_MOBS;
    }

    @Override
    public int getAmount() {
        return 30;
    }

    @Override
    public Map<LockoutTeam, Integer> getTrackerMap() {
        return LockoutServer.lockout.killedUndeadMobs;
    }

    @Override
    public List<Identifier> getTexturesToDisplay() {
        return TEXTURES;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        CycleTexturesProvider.super.renderTexture(context, x, y, tick);
        context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        tooltip.add("Undead Mobs killed: " + getTrackerMap().getOrDefault(team, 0) + "/" + getAmount());
        tooltip.add(" ");

        return tooltip;
    }

}
