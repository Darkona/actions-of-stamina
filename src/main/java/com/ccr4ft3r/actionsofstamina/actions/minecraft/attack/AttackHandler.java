package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public class AttackHandler {


    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
       /* ActionsOfStamina.log("onPlayerAttackEvent fired at {}", ActionsOfStamina.getSide(event.getEntity()) );

        var player = event.getEntity();
        boolean allowAttack = spendToAttack(player);
        event.setCanceled(!allowAttack);*/

    }


    /**
     * Swing weapon when player hits an entity
     *
     * @param event
     */
    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {

        if (event.isAttack()) {
            var player = Minecraft.getInstance().player;
            if (PlayerActions.cannotBeExhausted(player)) return;
            ActionsOfStamina.log("InteractionKeyMappingTriggered fired at {}, side: {}", event.isAttack(), ActionsOfStamina.getSide(player));
            HitResult hitResult = Minecraft.getInstance().hitResult;
            boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;
            boolean isMissHit = hitResult != null && hitResult.getType() == HitResult.Type.MISS;

            if (!allowAttack(player)) {
                boolean onlyForHits = AoSCommonConfig.ONLY_FOR_HITS.get();
                if (!onlyForHits && isMissHit || isEntityHit) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                    ActionsOfStamina.log("Attack and swing cancelled, Side: {}", ActionsOfStamina.getSide(player));

                }
            } else {
                ActionsOfStamina.log("Attack and swing allowed, Side: {}", ActionsOfStamina.getSide(player));

            }
        }
    }

    public static boolean allowAttack(Player player) {
        return player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).map(a ->
                a.getAction(AttackAction.actionName).map(w -> {
                    boolean can = w.canPerform(player);
                    ActionsOfStamina.log("onPlayerAttemptAttack: can attack?", can);
                    return can && w.perform(player);
                }).orElse(false)
        ).orElse(false);
    }

}
