package com.ccr4ft3r.actionsofstamina.compatibility.combatroll;

import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import net.combat_roll.api.event.ServerSideRollEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

final class CombatRollBridge {

    private CombatRollBridge() {
    }

    static void register() {
        ServerSideRollEvents.PLAYER_START_ROLLING.register(CombatRollBridge::onRoll);
    }

    private static void onRoll(ServerPlayer player, Vec3 velocity) {
        if (CombatRollCompat.ROLL.enabled()) PlayerActions.perform(player, CombatRollCompat.ROLL);
    }
}
