package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.capability.AoSCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.cannotBeExhausted;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public class ExhaustionHandler {


    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isCanceled() || cannotBeExhausted(player))
            return;
//        getPlayerData(player).set(JUMPING, !player.isInWater() && !player.onClimbable(), player);
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player) || cannotBeExhausted(event.player) || event.phase != TickEvent.Phase.END)
            return;

        player.getCapability(AoSCapabilities.PLAYER_ACTIONS).ifPresent(a -> {

            boolean isMoving = !player.position().equals(a.getLastPosition());
            boolean isCrawling = isMoving && !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
            boolean isClimbing = player.onClimbable() && isMoving;
            boolean isOnVehicle = player.getVehicle() != null;

            boolean isSwimming = player.isInWater() && !isOnVehicle && !isClimbing && isMoving;
            /*&& a.getLastPosition() != null && (player.position().x() != a.getLastPosition().x() || player.position().z() != a
                    .getLastPosition().z()));*/
            boolean isSneaking = player.isCrouching() && !isClimbing && isMoving;
            boolean isSprinting = player.isSprinting() && !isSwimming && !isOnVehicle && !isSneaking && !isClimbing && isMoving;
            boolean isFlying = player.getPose() == Pose.FALL_FLYING || player.getAbilities().flying;


            a.getAction(SprintAction.name).ifPresent(w -> {
                if (a.isSprinting() && !isSprinting) {
                    w.atStart(player);
                } else if (!a.isSprinting() && isSprinting) {
                    w.atFinish(player);
                }
            });

//            a.getAction(SwimAction.name).ifPresent(w -> {
//                if (a.isSwimming() && !isSwimming) {
//                    w.atFinish(player);
//                } else if (!a.isSwimming() && isSwimming) {
//                    w.atStart(player);
//                }
//            });

            a.setClimbing(isClimbing);
            a.setSwimming(isSwimming);
            a.setCrawling(isCrawling);
            a.setSneaking(isSneaking);
            a.setSprinting(isSprinting);
            a.setFlying(isFlying);

            a.setLastPosition(player.position());
            a.update(player);
        });


    }
}