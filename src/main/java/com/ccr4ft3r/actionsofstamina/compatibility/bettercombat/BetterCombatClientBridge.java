package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/** Direct Better Combat client calls; only reached through {@link BetterCombatCompat} on the client. */
final class BetterCombatClientBridge {

    private BetterCombatClientBridge() {
    }

    static void register() {
        BetterCombatClientEvents.ATTACK_START.register(BetterCombatClientBridge::onAttackStart);
    }

    /** A swing just started its upswing: cancel it if the stamina can't pay for it. */
    private static void onAttackStart(LocalPlayer player, AttackHand hand) {
        if (!BetterCombatCompat.isActive() || !BetterCombatConfig.BLOCK_WHEN_SHORT.getAsBoolean()) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (BetterCombatBridge.canAffordSwing(player, hand)) return;
        ((MinecraftClient_BetterCombat) Minecraft.getInstance()).cancelUpswing();
        ActionsOfStamina.log("Better Combat swing cancelled: not enough stamina");
    }
}
