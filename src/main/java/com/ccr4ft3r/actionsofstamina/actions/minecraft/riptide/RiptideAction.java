package com.ccr4ft3r.actionsofstamina.actions.minecraft.riptide;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class RiptideAction extends Action {

    public RiptideAction(ActionType type) {
        super(type);
    }

    public static boolean launchesOnRelease(ItemStack stack, Player player, int remainingTicks) {
        if (!(stack.getItem() instanceof TridentItem)) return false;
        return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME
                && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0 && player.isInWaterOrRain()
                && stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}
