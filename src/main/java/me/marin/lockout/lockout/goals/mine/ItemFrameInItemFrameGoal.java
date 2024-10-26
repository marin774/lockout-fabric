package me.marin.lockout.lockout.goals.mine;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemFrameInItemFrameGoal extends Goal implements TextureProvider {

    public ItemFrameInItemFrameGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Put Item Frame in an Item Frame";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/item_frame_in_item_frame.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
