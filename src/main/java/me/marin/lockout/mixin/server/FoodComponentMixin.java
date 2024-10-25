package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeamServer;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.opponent.OpponentEatsFoodGoal;
import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;

@Mixin(FoodComponent.class)
public class FoodComponentMixin {

    @Inject(method = "onConsume", at = @At("HEAD"))
    public void onConsume(World world, LivingEntity user, ItemStack itemStack, ConsumableComponent consumable, CallbackInfo ci) {

        if (world.isClient) return;

        if (!(user instanceof PlayerEntity player)) return;

        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        if (!lockout.isLockoutPlayer(player.getUuid())) return;
        LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(player.getUuid());


        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof ConsumeItemGoal consumeItemGoal) {
                if (consumeItemGoal.getItem().equals(itemStack.getItem())) {
                    lockout.completeGoal(goal, player);
                }
            }
            if (goal instanceof EatUniqueFoodsGoal eatUniqueFoodsGoal) {
                FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
                if (foodComponent != null) {
                    eatUniqueFoodsGoal.getTrackerMap().putIfAbsent(team, new LinkedHashSet<>());
                    eatUniqueFoodsGoal.getTrackerMap().get(team).add(foodComponent);

                    int size = eatUniqueFoodsGoal.getTrackerMap().get(team).size();

                    team.sendTooltipUpdate(eatUniqueFoodsGoal);
                    if (size >= eatUniqueFoodsGoal.getAmount()) {
                        lockout.completeGoal(goal, team);
                    }
                }
            }
            if (goal instanceof OpponentEatsFoodGoal) {
                lockout.complete1v1Goal(goal, player, false, player.getName().getString() + " ate food.");
            }
        }

    }
}
