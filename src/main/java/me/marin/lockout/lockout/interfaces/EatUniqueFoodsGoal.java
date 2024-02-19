package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static net.minecraft.item.FoodComponents.*;

public abstract class EatUniqueFoodsGoal extends Goal implements RequiresAmount, Trackable<LockoutTeam, LinkedHashSet<FoodComponent>>, CustomTextureRenderer, HasTooltipInfo {

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/eat_unique.png");
    private final ItemStack DISPLAY_ITEM_STACK = Items.APPLE.getDefaultStack();

    public EatUniqueFoodsGoal(String id, String data) {
        super(id, data);
        DISPLAY_ITEM_STACK.setCount(getAmount());
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, DISPLAY_ITEM_STACK, x, y);
        return true;
    }

    @Override
    public Map<LockoutTeam, LinkedHashSet<FoodComponent>> getTrackerMap() {
        return LockoutServer.lockout.foodTypesEaten;
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> lore = new ArrayList<>();
        var foods = getTrackerMap().getOrDefault(team, new LinkedHashSet<>());


        lore.add(" ");
        lore.add("Unique Food types: " + foods.size() + "/" + getAmount());
        lore.addAll(HasTooltipInfo.commaSeparatedList(foods.stream().map(EatUniqueFoodsGoal::foodComponentToString).toList()));
        lore.add(" ");

        return lore;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> lore = new ArrayList<>();

        lore.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            var foods = getTrackerMap().getOrDefault(team, new LinkedHashSet<>());
            lore.add(team.getColor() + team.getDisplayName() + ":" + Formatting.RESET + foods.size() + "/" + getAmount());
        }
        lore.add(" ");

        return lore;
    }

    private static String foodComponentToString(FoodComponent foodComponent) {
        if (foodComponent == APPLE) return "Apple";
        if (foodComponent == BAKED_POTATO) return "Baked Potato";
        if (foodComponent == BEEF) return "Beef";
        if (foodComponent == BEETROOT) return "Beetroot";
        if (foodComponent == BEETROOT_SOUP) return "Beetroot Soup";
        if (foodComponent == BREAD) return "Bread";
        if (foodComponent == CARROT) return "Carrot";
        if (foodComponent == CHICKEN) return "Chicken";
        if (foodComponent == CHORUS_FRUIT) return "Chorus Fruit";
        if (foodComponent == COD) return "Cod";
        if (foodComponent == COOKED_BEEF) return "Steak";
        if (foodComponent == COOKED_CHICKEN) return "Cooked Chicken";
        if (foodComponent == COOKED_COD) return "Cooked Cod";
        if (foodComponent == COOKED_MUTTON) return "Cooked Mutton";
        if (foodComponent == COOKED_PORKCHOP) return "Cooked Porkchop";
        if (foodComponent == COOKED_RABBIT) return "Cooked Rabbit";
        if (foodComponent == COOKED_SALMON) return "Cooked Salmon";
        if (foodComponent == COOKIE) return "Cookie";
        if (foodComponent == DRIED_KELP) return "Dried Kelp";
        if (foodComponent == ENCHANTED_GOLDEN_APPLE) return "Enchanted Golden Apple";
        if (foodComponent == GOLDEN_APPLE) return "Golden Apple";
        if (foodComponent == GOLDEN_CARROT) return "Golden Carrot";
        if (foodComponent == HONEY_BOTTLE) return "Honey Bottle";
        if (foodComponent == MELON_SLICE) return "Melon Slice";
        if (foodComponent == MUSHROOM_STEW) return "Mushroom Stew";
        if (foodComponent == MUTTON) return "Mutton";
        if (foodComponent == POISONOUS_POTATO) return "Poisonous Potato";
        if (foodComponent == PORKCHOP) return "Porkchop";
        if (foodComponent == POTATO) return "Potato";
        if (foodComponent == PUFFERFISH) return "Pufferfish";
        if (foodComponent == PUMPKIN_PIE) return "Pumpkin Pie";
        if (foodComponent == RABBIT) return "Rabbit";
        if (foodComponent == RABBIT_STEW) return "Rabbit Stew";
        if (foodComponent == ROTTEN_FLESH) return "Rotten Flesh";
        if (foodComponent == SALMON) return "Salmon";
        if (foodComponent == SPIDER_EYE) return "Spider Eye";
        if (foodComponent == SUSPICIOUS_STEW) return "Suspicious Stew";
        if (foodComponent == SWEET_BERRIES) return "Sweet Berries";
        if (foodComponent == GLOW_BERRIES) return "Glow Berries";
        if (foodComponent == TROPICAL_FISH) return "Tropical Fish";
        return "Unknown";
    }

}
