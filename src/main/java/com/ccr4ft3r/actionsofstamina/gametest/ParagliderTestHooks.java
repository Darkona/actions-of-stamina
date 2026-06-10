package com.ccr4ft3r.actionsofstamina.gametest;

import net.minecraft.server.level.ServerPlayer;
import tictim.paraglider.capabilities.PlayerMovement;

/** Paragliders calls for {@link CompatTests}; loaded only when Paragliders is. */
final class ParagliderTestHooks {

    private ParagliderTestHooks() {
    }

    static boolean hasMovement(ServerPlayer player) {
        return PlayerMovement.of(player) != null;
    }

    static int stamina(ServerPlayer player) {
        return PlayerMovement.of(player).getStamina();
    }

    static int maxStamina(ServerPlayer player) {
        return PlayerMovement.of(player).getMaxStamina();
    }
}
