package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ModConstants;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.ccr4ft3r.actionsofstamina.util.PlayerUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class ExhaustionHandler {

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.isCanceled() || cannotBeExhausted(player))
            return;
        ServerPlayerData playerData = getPlayerData(player);
        playerData.jump();
        exhaust(player, getProfile().forJumping, !player.isInWater() && !player.onClimbable() &&
            playerData.getJumps() >= getProfile().afterJumping.get(), getProfile().costsForJumping, playerData::resetJumps);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (cannotBeExhausted(player) || event.phase != TickEvent.Phase.END)
            return;

        ServerPlayerData playerData = getPlayerData(player);
        boolean isCrawling = PlayerUtil.isCrawling(player);
        boolean isMoving = playerData.isMoving() && !player.position().equals(playerData.getLastPosition());
        playerData.setLastPosition(player.position());

        boolean isInVehicle = player.getVehicle() != null;
        boolean isClimbing = player.onClimbable();
        boolean isSwimming = player.isInWater() && !isInVehicle && !isClimbing;
        boolean isSneaking = player.isCrouching() && !isClimbing;
        boolean isSprinting = player.isSprinting() && !isSwimming && !isInVehicle && !isSneaking && !isClimbing;
        playerData.setSprinting(isSprinting);
        playerData.setCrawling(isCrawling);
        playerData.setBlocking(player.isBlocking());

        if (getProfile().forBlocking.get() && !PlayerUtil.hasEnoughFeathers(getProfile().costsForBlocking, getProfile().minForBlocking, (ServerPlayer) player)
            && player.getMainHandItem().is(Tags.Items.TOOLS_SHIELDS))
            player.stopUsingItem();
        exhaust(player, getProfile().forBlocking, player.isBlocking() && playerData.getBlockingTicks()
            >= getProfile().afterBlocking.get(), getProfile().costsForBlocking, playerData::resetBlockingTicks);

        if (!isMoving)
            return;

        exhaust(player, getProfile().forSprinting, isSprinting && playerData.getSprintingTicks()
            >= getProfile().afterSprinting.get(), getProfile().costsForSprinting, playerData::resetSprintingTicks);
        exhaust(player, getProfile().forCrawling, isCrawling && playerData.getCrawlingTicks()
            >= getProfile().afterCrawling.get(), getProfile().costsForCrawling, playerData::resetCrawlingTicks);
    }
}