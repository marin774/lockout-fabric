package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.interfaces.WearArmorPieceGoal;
import me.marin.lockout.mixin.server.PlayerInventoryAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WearColoredLeatherPieceGoal extends WearArmorPieceGoal {

    private final Item ITEM;
    private final ItemStack DISPLAY_ITEM_STACK;
    private final int COLOR;
    private final String GOAL_NAME;

    public WearColoredLeatherPieceGoal(String id, String data) {
        super(id, data);

        String[] parts = data.split(GoalDataConstants.DATA_SEPARATOR);
        ITEM = GoalDataConstants.getLeatherArmor(parts[0]);
        DyeColor DYE_COLOR = GoalDataConstants.getDyeColor(parts[1]);
        COLOR = GoalDataConstants.getDyeColorValue(DYE_COLOR);

        DISPLAY_ITEM_STACK = ITEM.getDefaultStack();
        DISPLAY_ITEM_STACK.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(COLOR));

        GOAL_NAME = "Wear " + GoalDataConstants.getDyeColorFormatted(DYE_COLOR) + " " + GoalDataConstants.getArmorPieceFormatted(parts[0]);
    }

    @Override
    public String getGoalName() {
        return GOAL_NAME;
    }

    @Override
    public List<Item> getItems() {
        return List.of(ITEM);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        return false;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {

        var armor = new ArrayList<ItemStack>();
        armor.add(((PlayerInventoryAccessor)playerInventory).getEquipment().get(EquipmentSlot.HEAD));
        armor.add(((PlayerInventoryAccessor)playerInventory).getEquipment().get(EquipmentSlot.CHEST));
        armor.add(((PlayerInventoryAccessor)playerInventory).getEquipment().get(EquipmentSlot.LEGS));
        armor.add(((PlayerInventoryAccessor)playerInventory).getEquipment().get(EquipmentSlot.FEET));

        for (ItemStack item : armor) {
            if (item == null) continue;
            if (item.getItem().equals(ITEM)) {
                if (Optional.ofNullable(item.get(DataComponentTypes.DYED_COLOR)).map(dyed -> dyed.rgb() == COLOR).orElse(false)) {
                    return true;
                }
            }
        }
        return false;
    }

}
