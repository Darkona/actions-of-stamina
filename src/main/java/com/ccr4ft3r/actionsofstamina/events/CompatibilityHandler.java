package com.ccr4ft3r.actionsofstamina.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tictim.paraglider.api.movement.ParagliderPlayerStates;
import tictim.paraglider.forge.capability.PlayerMovementProvider;
import tictim.paraglider.impl.movement.PlayerMovement;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.PARAGLIDING;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.getPlayerData;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.cannotBeExhausted;

public class CompatibilityHandler {

    @SubscribeEvent
    public static void onParagliding(TickEvent.PlayerTickEvent event) {
        if (cannotBeExhausted(event.player) || event.phase != TickEvent.Phase.END)
            return;
        ServerPlayer player = (ServerPlayer) event.player;
        LazyOptional<PlayerMovement> movement = player.getCapability(PlayerMovementProvider.PLAYER_MOVEMENT);
        getPlayerData(player)
                .set(PARAGLIDING, movement.map(m -> m.state().flags().contains(ParagliderPlayerStates.PARAGLIDING))
                                          .orElse(false), player);
    }

}