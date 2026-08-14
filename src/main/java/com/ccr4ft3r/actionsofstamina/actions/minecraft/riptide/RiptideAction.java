package com.ccr4ft3r.actionsofstamina.actions.minecraft.riptide;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Launching with a Riptide trident, charged when it's released (a Riptide trident is never thrown, so this isn't a
 * throw). A launch the player can't afford doesn't happen: the aim just ends.
 */
public class RiptideAction extends Action {

    public RiptideAction(ActionType type) {
        super(type);
    }

    /**
     * Whether releasing the item with {@code remainingTicks} of use left launches the player, as the trident itself
     * decides it: a trident (modded ones extending it too) with Riptide, a full aim, in water or rain.
     */
    public static boolean launchesOnRelease(ItemStack stack, Player player, int remainingTicks) {
        if (!(stack.getItem() instanceof TridentItem)) return false;
        return stack.getUseDuration() - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME
                && EnchantmentHelper.getRiptide(stack) > 0 && player.isInWaterOrRain();
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}
