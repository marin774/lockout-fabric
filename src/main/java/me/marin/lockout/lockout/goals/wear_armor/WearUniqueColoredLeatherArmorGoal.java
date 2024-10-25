package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.interfaces.WearArmorGoal;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WearUniqueColoredLeatherArmorGoal extends WearArmorGoal {

    private static final List<Item> ITEMS = List.of(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);

    public WearUniqueColoredLeatherArmorGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear Full Leather Armor, all in different colors";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        if (!super.satisfiedBy(playerInventory)) return false;

        Set<Integer> colors = new HashSet<>();
        for (ItemStack itemStack : playerInventory.armor) {
            DyedColorComponent dyedColor = itemStack.get(DataComponentTypes.DYED_COLOR);
            if (dyedColor == null) continue;
            int color = dyedColor.rgb();
            if (color != DyedColorComponent.DEFAULT_COLOR) {
                colors.add(color);
            }
        }

        return colors.size() == 4;
    }

    private int lastTickColorChanged = -1;
    private int color = 0;
    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        int mod = tick % (60 * getItemsToDisplay().size());
        ItemStack itemStack = getItemsToDisplay().get(mod / 60).getDefaultStack();

        int colorChange = tick / 60;
        if (lastTickColorChanged != colorChange) {
            lastTickColorChanged = colorChange;
            color = (Lockout.random.nextInt(0, 256) << 16) | (Lockout.random.nextInt(0, 256) << 8) | (Lockout.random.nextInt(0, 256));
        }

        itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, true));
        context.drawItem(itemStack, x, y);
        return true;
    }

}
