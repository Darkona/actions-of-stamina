package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.CombatFlags;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Direct Better Combat calls (common side); only reached through {@link BetterCombatCompat} when it is loaded. */
final class BetterCombatBridge {

    static final ResourceLocation SOURCE = ActionsOfStamina.id("bettercombat/swing");

    private BetterCombatBridge() {
    }

    static boolean hasAttacks(ItemStack stack) {
        if (stack.isEmpty()) return false;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(stack);
        return attributes != null && attributes.attacks() != null;
    }

    /** Stamina one swing costs, with the two-handed, off-hand and combo-finisher multipliers. */
    static int swingCost(AttackHand hand) {
        double multiplier = 1.0;
        if (hand.isOffHand()) multiplier *= BetterCombatConfig.OFF_HAND_MULTIPLIER.getAsDouble();
        WeaponAttributes attributes = hand.attributes();
        if (attributes != null && attributes.isTwoHanded()) multiplier *= BetterCombatConfig.TWO_HANDED_MULTIPLIER.getAsDouble();
        ComboState combo = hand.combo();
        if (combo != null && combo.total() > 1 && combo.current() == combo.total()) {
            multiplier *= BetterCombatConfig.COMBO_FINISHER_MULTIPLIER.getAsDouble();
        }
        return (int) Math.round(BetterCombatConfig.SWING.cost() * multiplier);
    }

    static boolean chargeSwing(ServerPlayer player, int comboCount, boolean onlyCheck) {
        if (player.isCreative() || player.isSpectator() || CombatFlags.isAttackDisabled(player)) return true;
        AttackHand hand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (hand == null) return true;
        int cost = swingCost(hand);
        if (cost <= 0) return true;
        StaminaBackend backend = StaminaBackends.server();
        boolean paid = onlyCheck ? backend.canSpend(player, SOURCE, cost)
                : backend.spend(player, SOURCE, cost, BetterCombatConfig.SWING.regenDelay());
        return paid || !BetterCombatConfig.BLOCK_WHEN_SHORT.getAsBoolean();
    }

    /** Client check before a swing starts, against the local player's synced stamina. */
    static boolean canAffordSwing(Player player, AttackHand hand) {
        int cost = swingCost(hand);
        if (cost <= 0) return true;
        StaminaBackend backend = StaminaBackends.of(player);
        return backend.canSpend(player, SOURCE, cost);
    }
}
