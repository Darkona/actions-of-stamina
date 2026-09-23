package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.vessel.VesselContainer;

/** Direct Paragliders calls; only reached through {@link ParagliderCompat} when Paragliders is loaded. */
final class ParagliderBridge {

    private ParagliderBridge() {
    }

    static boolean isParagliding(Player player) {
        Movement movement = Movement.get(player);
        return movement != null && movement.state().paragliding();
    }

    /**
     * Server: listens to the player's vessel container once (it lives as long as the player entity, dimension changes
     * included) and returns its stamina vessels. Paragliders calls the listener on every real change, commands and
     * bargains included, never for simulations.
     */
    static int listenAndCountVessels(ServerPlayer player) {
        VesselContainer vessels = VesselContainer.get(player);
        PlayerActions actions = PlayerActions.get(player);
        if (actions.vesselListenerTarget() != vessels) {
            actions.setVesselListenerTarget(vessels);
            vessels.onChange((type, change, playEffect) -> {
                if (type == VesselContainer.ActionType.STAMINA_VESSEL) ParagliderCompat.applyVessels(player, vessels.staminaVessel());
            });
        }
        return vessels.staminaVessel();
    }
}
