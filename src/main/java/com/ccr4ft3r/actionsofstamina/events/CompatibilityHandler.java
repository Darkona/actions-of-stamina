package com.ccr4ft3r.actionsofstamina.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tictim.paraglider.capabilities.Caps;
import tictim.paraglider.capabilities.PlayerMovement;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.*;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

public class CompatibilityHandler {

    @SubscribeEvent
    public static void onParagliding(TickEvent.PlayerTickEvent event) {
        if (cannotBeExhausted(event.player))
            return;
        ServerPlayer player = (ServerPlayer) event.player;
        LazyOptional<PlayerMovement> movement = player.getCapability(Caps.playerMovement);
        getPlayerData(player).set(PARAGLIDING, movement.map(PlayerMovement::isParagliding).orElse(false), player);
    }
}