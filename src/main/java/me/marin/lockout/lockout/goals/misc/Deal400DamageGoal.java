package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import me.marin.lockout.lockout.texture.TextureProvider;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Deal400DamageGoal extends Goal implements TextureProvider, CustomTextureRenderer, HasTooltipInfo {

    private final static ItemStack DISPLAY_ITEM_STACK = Items.RED_DYE.getDefaultStack();
    static {
        DISPLAY_ITEM_STACK.setCount(64);
    }
    public Deal400DamageGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Deal 400 damage";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/deal_400_damage.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, DISPLAY_ITEM_STACK, x, y, "400");
        return true;
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> lore = new ArrayList<>();
        double damage = LockoutServer.lockout.damageDealt.getOrDefault(team, 0.0);

        lore.add(" ");
        lore.add("Damage: " + Math.min(400, (int) damage) + "/400");
        lore.add(" ");

        return lore;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> lore = new ArrayList<>();

        lore.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            double damage = LockoutServer.lockout.damageDealt.getOrDefault(team, 0.0);
            lore.add(team.getColor() + team.getDisplayName() + ":" + Formatting.RESET + Math.min(400, (int) damage) + "/400");
        }
        lore.add(" ");

        return lore;
    }

}
