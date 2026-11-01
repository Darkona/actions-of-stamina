package com.ccr4ft3r.actionsofstamina.compat.paraglider;

import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.movement.ParagliderPlayerStates;
import tictim.paraglider.api.vessel.VesselContainer;

final class ParagliderBridge {

    private ParagliderBridge() {
    }

    static boolean isParagliding(Player player) {
        Movement movement = Movement.get(player);
        return movement != null && movement.state().has(ParagliderPlayerStates.Flags.FLAG_PARAGLIDING);
    }

    static int staminaVessels(Player player) {
        return VesselContainer.get(player).staminaVessel();
    }
}
