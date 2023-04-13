package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ModConstants;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.ccr4ft3r.actionsofstamina.util.PlayerUtil;
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
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

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
    public static void onPlayerAttack(AttackEntityEvent event) {
        exhaustForWeaponSwing(event.isCanceled(), event.getEntity());
    }

    public static void exhaustForWeaponSwing(boolean execption, Player player) {
        if (execption || cannotBeExhausted(player))
            return;
        ServerPlayerData playerData = getPlayerData(player);
        ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(itemstack.getItem() instanceof TieredItem))
            return;
        Multimap<Attribute, AttributeModifier> modifiers = itemstack.getItem().getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
        double multiplier = 1d;
        Iterator<AttributeModifier> attackSpeeds = modifiers.get(Attributes.ATTACK_SPEED).iterator();
        if (attackSpeeds.hasNext()) {
            AttributeModifier attackSpeed = attackSpeeds.next();
            multiplier = getProfile().attackSpeedMultiplier.get() * (1.6 - (attackSpeed.getAmount() + 4));
        }
        playerData.attack(Math.max(0, 1 + multiplier));
        exhaust(player, getProfile().forAttacking,
            playerData.getAttacks() >= getProfile().afterAttacking.get(), getProfile().costsForAttacking, playerData::resetAttacks);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (cannotBeExhausted(event.player) || event.phase != TickEvent.Phase.END)
            return;
        ServerPlayer player = (ServerPlayer) event.player;
        ServerPlayerData playerData = getPlayerData(player);
        boolean isCrawling = PlayerUtil.isCrawling(player);
        boolean isMoving = playerData.isMoving() && !player.position().equals(playerData.getLastPosition());
        playerData.setLastPosition(player.position());

        boolean isInVehicle = player.getVehicle() != null;
        boolean isClimbing = player.onClimbable();
        boolean isSwimming = player.isInWater() && !isInVehicle && !isClimbing;
        boolean isSneaking = player.isCrouching() && !isClimbing;
        boolean isSprinting = player.isSprinting() && !isSwimming && !isInVehicle && !isSneaking && !isClimbing;
        boolean isFlying = player.getPose() == Pose.FALL_FLYING || player.getAbilities().flying;
        playerData.setSprinting(isSprinting, player);
        playerData.setCrawling(isCrawling, player);
        playerData.setBlocking(player.isBlocking(), player);
        if (getProfile().forFlying.get() && !PlayerUtil.hasEnoughFeathers(getProfile().costsForFlying, getProfile().minForFlying, (ServerPlayer) player)
            && playerData.isFlying()) {
            player.getAbilities().flying = false;
            player.getAbilities().mayfly = false;
            player.onUpdateAbilities();
        }
        playerData.setFlying(isFlying, player);

        if (getProfile().forBlocking.get() && !PlayerUtil.hasEnoughFeathers(getProfile().costsForBlocking, getProfile().minForBlocking, (ServerPlayer) player)
            && player.getMainHandItem().getItem() instanceof ShieldItem)
            player.stopUsingItem();
        exhaust(player, getProfile().forBlocking, player.isBlocking() && playerData.getBlockingTicks()
            >= getProfile().afterBlocking.get(), getProfile().costsForBlocking, playerData::resetBlockingTicks);
        exhaust(player, getProfile().forFlying, isFlying && playerData.getFlyingTicks()
            >= getProfile().afterFlying.get(), getProfile().costsForFlying, playerData::resetFlyingTicks);

        if (!isMoving)
            return;

        exhaust(player, getProfile().forSprinting, isSprinting && playerData.getSprintingTicks()
            >= getProfile().afterSprinting.get(), getProfile().costsForSprinting, playerData::resetSprintingTicks);
        exhaust(player, getProfile().forCrawling, isCrawling && playerData.getCrawlingTicks()
            >= getProfile().afterCrawling.get(), getProfile().costsForCrawling, playerData::resetCrawlingTicks);
    }
}