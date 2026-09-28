package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.movement.Movement;

/** Direct Paragliders calls; only reached through {@link ParagliderCompat} when Paragliders is loaded. */
final class ParagliderBridge {

    private ParagliderBridge() {
    }

    static boolean isParagliding(Player player) {
        Movement movement = Movement.get(player);
        return movement != null && movement.state().paragliding();
    }
}
