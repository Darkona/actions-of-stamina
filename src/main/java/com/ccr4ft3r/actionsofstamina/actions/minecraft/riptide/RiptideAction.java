package com.ccr4ft3r.actionsofstamina.actions.minecraft.riptide;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Launching with a Riptide trident, charged when it's released (a Riptide trident is never thrown, so this isn't a
 * throw). A launch the player can't afford doesn't happen: the aim just ends.
 */
public class RiptideAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("riptide");

    public RiptideAction() {
        super(SOURCE, AoSServerConfig.RIPTIDE);
    }

    /**
     * Whether releasing the item with {@code remainingTicks} of use left launches the player, as the trident itself
     * decides it: a trident (modded ones extending it too) with Riptide, a full aim, in water or rain, and not about to
     * break.
     */
    public static boolean launchesOnRelease(ItemStack stack, Player player, int remainingTicks) {
        if (!(stack.getItem() instanceof TridentItem)) return false;
        return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME
                && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0 && player.isInWaterOrRain()
                && stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    @Override
    public int id() {
        return RIPTIDE;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}
