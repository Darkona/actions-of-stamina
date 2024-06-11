package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ModConstants;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.ccr4ft3r.actionsofstamina.util.PlayerUtil;
import com.darkona.feathers.api.FeathersAPI;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Objects;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.*;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.getProfile;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.getPlayerData;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.cannotBeExhausted;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class ExhaustionHandler {

    public static final String FEATHERS_EXHAUSTION = "f5dfc430-96b5-4fa3-a0d4-551e3e8a01d1";
    public static final AttributeModifier Exhaustion = new AttributeModifier(FEATHERS_EXHAUSTION, -1, AttributeModifier.Operation.ADDITION);

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isCanceled() || cannotBeExhausted(player))
            return;
        getPlayerData(player).set(JUMPING, !player.isInWater() && !player.onClimbable(), player);
    }



    private static void changeAttackByFeathers(Player player) {
        if (FeathersAPI.getAvailableFeathers(player) < 4) {
            Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).addTransientModifier(Exhaustion);
        } else {
            Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).removeModifier(Exhaustion);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player) || cannotBeExhausted(event.player) || event.phase != TickEvent.Phase.END)
            return;

        ServerPlayerData playerData = getPlayerData(player);
        boolean isCrawling = PlayerUtil.isCrawling(player);
        boolean isMoving = playerData.isMoving() && !player.position().equals(playerData.getLastPosition());
        boolean isInVehicle = player.getVehicle() != null;
        boolean isClimbing = player.onClimbable() && isMoving;
        boolean isSwimming = player.isInWater() && !isInVehicle && !isClimbing && isMoving
                && playerData.getLastPosition() != null && (player.position().x() != playerData.getLastPosition().x() || player.position().z() != playerData
                .getLastPosition().z());
        boolean isSneaking = player.isCrouching() && !isClimbing && isMoving;
        boolean isSprinting = player.isSprinting() && !isSwimming && !isInVehicle && !isSneaking && !isClimbing && isMoving;
        boolean isFlying = player.getPose() == Pose.FALL_FLYING || player.getAbilities().flying;

        playerData.setLastPosition(player.position());
        playerData.set(SPRINTING, isSprinting, player);
        playerData.set(CRAWLING, isCrawling, player);
        playerData.set(HOLDING_THE_SHIELD, player.isBlocking() && player.getMainHandItem().getItem() instanceof ShieldItem, player);
        playerData.set(FLYING, isFlying, player);
        playerData.set(SWIMMING, isSwimming, player);
        playerData.update(player);
    }
}