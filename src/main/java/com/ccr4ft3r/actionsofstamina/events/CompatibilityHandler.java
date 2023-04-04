package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tictim.paraglider.capabilities.Caps;
import tictim.paraglider.capabilities.PlayerMovement;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

public class CompatibilityHandler {

    @SubscribeEvent
    public static void onParagliding(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (cannotBeExhausted(player))
            return;

        LazyOptional<PlayerMovement> movement = player.getCapability(Caps.playerMovement);
        boolean isParagliding = movement.map(PlayerMovement::isParagliding).orElse(false);

        if (getProfile().forParagliding.get() && !hasEnoughFeathers(getProfile().costsForParagliding, getProfile().minForParagliding) && isParagliding) {
            movement.ifPresent(m -> m.setDepleted(true));
            isParagliding = false;
        }

        ServerPlayerData playerData = getPlayerData(player);
        playerData.setParagliding(isParagliding);
        exhaust(player, getProfile().forParagliding, isParagliding && playerData.getParaglidingTicks()
            >= getProfile().afterParagliding.get(), getProfile().costsForParagliding, playerData::resetParaglidingTicks);
    }
}