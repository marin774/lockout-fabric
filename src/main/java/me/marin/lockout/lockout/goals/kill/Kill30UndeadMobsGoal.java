package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.Constants;
import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.interfaces.KillSpecificMobsGoal;
import me.marin.lockout.lockout.texture.CycleTexturesProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Kill30UndeadMobsGoal extends KillSpecificMobsGoal implements CycleTexturesProvider {

    private static final ItemStack ITEM_STACK = Items.WOODEN_SWORD.getDefaultStack();
    static {
        ITEM_STACK.setCount(30);
    }
    private static final List<Identifier> TEXTURES = List.of(
            new Identifier(Constants.NAMESPACE, "textures/custom/undead/kill_zombie.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/undead/kill_wither_skeleton.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/undead/kill_zombie_villager.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/undead/kill_drowned.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/undead/kill_husk.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/undead/kill_stray.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/undead/kill_zoglin.png")
    );
    // undead: drowned, husk, phantom, skeleton, skeletonhorse, stray, wither, wither skeleton, zoglin, zombie, zombie horse, zombie villager, zombiefied piglin
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
            EntityType.ZOMBIFIED_PIGLIN
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
        return Lockout.getInstance().killedUndeadMobs;
    }

    @Override
    public List<Identifier> getTexturesToDisplay() {
        return TEXTURES;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        CycleTexturesProvider.super.renderTexture(context, x, y, tick);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> lore = new ArrayList<>();

        lore.add(" ");
        lore.add("Undead Mobs killed: " + getTrackerMap().getOrDefault(team, 0) + "/" + getAmount());
        lore.add(" ");

        return lore;
    }

}
