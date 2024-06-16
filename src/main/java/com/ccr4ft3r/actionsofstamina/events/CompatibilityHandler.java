package com.ccr4ft3r.actionsofstamina.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CompatibilityHandler {

    @SubscribeEvent
    public static void onParagliding(TickEvent.PlayerTickEvent event) {
        /*if (cannotBeExhausted(event.player) || event.phase != TickEvent.Phase.END)
            return;
        ServerPlayer player = (ServerPlayer) event.player;
        LazyOptional<PlayerMovement> movement = player.getCapability(PlayerMovementProvider.PLAYER_MOVEMENT);
        getPlayerData(player)
                .set(PARAGLIDING, movement.map(m -> m.state().flags().contains(ParagliderPlayerStates.PARAGLIDING))
                                          .orElse(false), player);*/
    }

}