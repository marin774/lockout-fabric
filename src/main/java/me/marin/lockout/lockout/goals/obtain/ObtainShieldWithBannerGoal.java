package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.ObtainItemsGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import me.marin.lockout.mixin.server.PlayerInventoryAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.List;

public class ObtainShieldWithBannerGoal extends ObtainItemsGoal implements TextureProvider {

    public ObtainShieldWithBannerGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Put a Banner on a Shield";
    }

    @Override
    public List<Item> getItems() {
        return null;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        for (var defaultedList : ((PlayerInventoryAccessor) playerInventory).getCombinedInventory()) {
            for (ItemStack item : defaultedList) {
                if (item == null) continue;
                if (item.isEmpty()) continue;
                if (item.getItem().equals(Items.SHIELD)) {
                    NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(item);
                    if (nbtCompound != null && nbtCompound.contains("Base")) return true;
                }
            }
        }
        return false;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/apply_banner_shield.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        return TextureProvider.super.renderTexture(context, x, y, tick);
    }
}
