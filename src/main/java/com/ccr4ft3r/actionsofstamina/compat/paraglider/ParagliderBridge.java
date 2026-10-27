package com.ccr4ft3r.actionsofstamina.compat.paraglider;

import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.vessel.VesselContainer;

final class ParagliderBridge {

    private ParagliderBridge() {
    }

    static boolean isParagliding(Player player) {
        Movement movement = Movement.get(player);
        return movement != null && movement.state().paragliding();
    }

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
