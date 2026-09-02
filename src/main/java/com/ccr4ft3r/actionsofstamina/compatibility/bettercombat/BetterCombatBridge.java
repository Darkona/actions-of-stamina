package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Direct Better Combat calls (common side); only reached through {@link BetterCombatCompat} when it is loaded. */
final class BetterCombatBridge {

    /**
     * Category multiplier per weapon attributes (one instance per weapon in Better Combat's registry, replaced when it
     * reloads). Cleared on config and datapack reloads. Locked: the server, network and client threads read it.
     */
    private static final Reference2DoubleOpenHashMap<WeaponAttributes> CATEGORY_CACHE = new Reference2DoubleOpenHashMap<>();

    static {
        CATEGORY_CACHE.defaultReturnValue(-1.0);
    }

    private BetterCombatBridge() {
    }

    static boolean hasAttacks(ItemStack stack) {
        if (stack.isEmpty()) return false;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(stack);
        return attributes != null && attributes.attacks() != null;
    }

    static void clearCategoryCache() {
        synchronized (CATEGORY_CACHE) {
            CATEGORY_CACHE.clear();
        }
    }

    /** The weapon's category multiplier, looked up once per weapon type. */
    private static double categoryMultiplier(WeaponAttributes attributes) {
        synchronized (CATEGORY_CACHE) {
            double multiplier = CATEGORY_CACHE.getDouble(attributes);
            if (multiplier < 0) {
                multiplier = BetterCombatConfig.categoryMultiplier(attributes.category());
                CATEGORY_CACHE.put(attributes, multiplier);
            }
            return multiplier;
        }
    }

    /** Stamina one swing costs, with the category, two-handed, off-hand and combo-finisher multipliers. */
    static int swingCost(AttackHand hand) {
        double multiplier = 1.0;
        if (hand.isOffHand()) multiplier *= BetterCombatConfig.OFF_HAND_MULTIPLIER.get();
        WeaponAttributes attributes = hand.attributes();
        if (attributes != null) {
            multiplier *= categoryMultiplier(attributes);
            if (attributes.isTwoHanded()) multiplier *= BetterCombatConfig.TWO_HANDED_MULTIPLIER.get();
        }
        ComboState combo = hand.combo();
        if (combo != null && combo.total() > 1 && combo.current() == combo.total()) {
            multiplier *= BetterCombatConfig.COMBO_FINISHER_MULTIPLIER.get();
        }
        return (int) Math.round(BetterCombatConfig.SWING.cost() * multiplier);
    }

    static boolean chargeSwing(ServerPlayer player, int comboCount, boolean onlyCheck) {
        if (player.isCreative() || player.isSpectator()) return true;
        AttackHand hand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (hand == null) return true;
        int cost = swingCost(hand);
        Action swing = PlayerActions.get(player).getAction(BetterCombatCompat.SWING);
        if (cost <= 0 || swing == null) return true;
        boolean paid = onlyCheck ? swing.canPay(player, cost) : swing.pay(player, cost);
        return paid || !BetterCombatConfig.BLOCK_WHEN_SHORT.get();
    }

    /** Client check before a swing starts, against the local player's synced stamina. */
    static boolean canAffordSwing(Player player, AttackHand hand) {
        int cost = swingCost(hand);
        Action swing = PlayerActions.get(player).getAction(BetterCombatCompat.SWING);
        return cost <= 0 || swing == null || swing.canPay(player, cost);
    }
}
