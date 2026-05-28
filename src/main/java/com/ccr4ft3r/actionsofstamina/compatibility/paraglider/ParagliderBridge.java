package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.movement.ParagliderPlayerStates;
import tictim.paraglider.api.vessel.VesselContainer;

/** Direct Paragliders calls; only reached through {@link ParagliderCompat} when Paragliders is loaded. */
final class ParagliderBridge {

    private ParagliderBridge() {
    }

    static boolean isParagliding(Player player) {
        Movement movement = Movement.get(player);
        return movement != null && movement.state().has(ParagliderPlayerStates.Flags.FLAG_PARAGLIDING);
    }

    /** The player's Stamina Vessels (a capability lookup: on joining a level, not every tick). */
    static int staminaVessels(Player player) {
        return VesselContainer.get(player).staminaVessel();
    }
}
