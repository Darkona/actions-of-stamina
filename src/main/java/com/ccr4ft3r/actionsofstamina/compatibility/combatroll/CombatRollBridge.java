package com.ccr4ft3r.actionsofstamina.compatibility.combatroll;

import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.combatroll.api.event.ServerSideRollEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/** Direct Combat Roll calls; only reached through {@link CombatRollCompat} when Combat Roll is loaded. */
final class CombatRollBridge {

    private CombatRollBridge() {
    }

    static void register() {
        ServerSideRollEvents.PLAYER_START_ROLLING.register(CombatRollBridge::onRoll);
    }

    /** Server main thread; the roll already happened client side, so it can only be charged, not stopped. */
    private static void onRoll(ServerPlayer player, Vec3 velocity) {
        if (CombatRollCompat.isActive()) PlayerActions.perform(player, CombatRollCompat.ROLL);
    }
}
