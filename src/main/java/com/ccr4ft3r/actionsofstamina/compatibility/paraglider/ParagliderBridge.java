package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import net.minecraft.world.entity.player.Player;
import tictim.paraglider.capabilities.PlayerMovement;

/** Direct Paragliders calls; only reached through {@link ParagliderCompat} when Paragliders is loaded. */
final class ParagliderBridge {

    private ParagliderBridge() {
    }

    static boolean isParagliding(Player player) {
        PlayerMovement movement = PlayerMovement.of(player);
        return movement != null && movement.isParagliding();
    }

    /** The player's Stamina Vessels (a capability lookup: on joining a level, not every tick). */
    static int staminaVessels(Player player) {
        PlayerMovement movement = PlayerMovement.of(player);
        return movement == null ? 0 : movement.getStaminaVessels();
    }
}
