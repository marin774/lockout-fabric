package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.interfaces.WearArmorGoal;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class WearFullEnchantedArmorGoal extends WearArmorGoal {

    private static final List<Item> HELMETS = List.of(Items.LEATHER_HELMET, Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET);
    private static final List<Item> CHESTPLATES = List.of(Items.LEATHER_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE);
    private static final List<Item> LEGGINGS = List.of(Items.LEATHER_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS);
    private static final List<Item> BOOTS = List.of(Items.LEATHER_BOOTS, Items.GOLDEN_BOOTS, Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS);
    private static final List<List<Item>> ITEMS = List.of(HELMETS, CHESTPLATES, LEGGINGS, BOOTS);

    public WearFullEnchantedArmorGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear Full Enchanted Armor";
    }

    @Override
    public List<Item> getItems() {
        return null;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        for (ItemStack itemStack : playerInventory.armor) {
            if (itemStack.isEmpty() || !itemStack.hasEnchantments()) {
                return false;
            }
        }

        return true;
    }

    private int lastTickArmorChanged = -1;
    private Item armorPiece;
    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        List<Item> itemType = ITEMS.get(tick % 240 / 60);

        int armorChange = tick / 60;
        if (lastTickArmorChanged != armorChange) {
            lastTickArmorChanged = armorChange;
            armorPiece = itemType.get(Lockout.random.nextInt(itemType.size()));
        }

        ItemStack stack = armorPiece.getDefaultStack();
        stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        context.drawItem(stack, x, y);
        return true;
    }

}