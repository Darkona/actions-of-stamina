package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.api.FeathersAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public class AttackHandler {


    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if(AoSCommonConfig.ENABLE_DEBUGGING.get())
            ActionsOfStamina.logger.info("onPlayerAttack executed at {}", event.getEntity().level().isClientSide ? "Client" : "Server" );


        var player = event.getEntity();
        boolean allowAttack = spendToAttack(player);
        event.setCanceled(!allowAttack);
    }

    public static boolean spendToAttack(Player player) {
        var result = new AtomicBoolean(true);
        player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> {
            a.getAction(AttackAction.actionName).ifPresent(w -> {
                if (w.canPerform(player)) {
                    result.set(w.perform(player));
                }
            });
        });
        return result.get();
    }

    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {

        var player = Minecraft.getInstance().player;
        if (PlayerActions.cannotBeExhausted(player)) return;

        if (event.isAttack()) {


            player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> {
                a.getAction(AttackAction.actionName).ifPresent(w -> {

                    boolean hasEnoughFeathers = FeathersAPI.canSpendFeathers(player, w.getCost());
                    if(AoSCommonConfig.ENABLE_DEBUGGING.get())
                        ActionsOfStamina.logger.info("Has enough feathers: " + hasEnoughFeathers);
                    HitResult hitResult = Minecraft.getInstance().hitResult;
                    boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;

                    if (!hasEnoughFeathers && (!AoSCommonConfig.ONLY_FOR_HITS.get() || isEntityHit)) {
                        event.setCanceled(true);
                        event.setSwingHand(false);
                        if(AoSCommonConfig.ENABLE_DEBUGGING.get())
                            ActionsOfStamina.logger.info("Attack cancelled!!!");
                    }
                });
            });
        }
    }



}
