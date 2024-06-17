package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraFlightAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.capability.AoSCapabilities;
import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import com.darkona.feathers.api.ICapabilityPlugin;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public class AosFeathersPlugin implements ICapabilityPlugin {

    private static AosFeathersPlugin INSTANCE;

    private AosFeathersPlugin() {

    }
    public static ICapabilityPlugin getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AosFeathersPlugin();
        }
        return INSTANCE;
    }

    @Override
    public void onPlayerJoin(EntityJoinLevelEvent entityJoinLevelEvent) {
        if (entityJoinLevelEvent.getEntity() instanceof Player player) {
            player.getCapability(AoSCapabilities.PLAYER_ACTIONS).ifPresent(a -> {
                ActionProvider.getInstance().addEnabledActions(a, player);
            });
        }
    }

    @Override
    public void onPlayerTickBefore(TickEvent.PlayerTickEvent event) {
        if (IActionCapability.cannotBeExhausted(event.player)) return;

        var player = event.player;

        player.getCapability(AoSCapabilities.PLAYER_ACTIONS).ifPresent(a -> {

            boolean isMoving = a.isClientMoving() && !player.position().equals(a.getLastPosition());
            boolean isCrawling = isMoving && !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
            boolean isClimbing = player.onClimbable() && isMoving;
            boolean isOnVehicle = player.getVehicle() != null;

            boolean isSwimming = player.isInWater() && !isOnVehicle && !isClimbing && isMoving;
            boolean isSprinting = player.isSprinting() && !isOnVehicle && isMoving && player.onGround();
            /*&& a.getLastPosition() != null && (player.position().x() != a.getLastPosition().x() || player.position().z() != a
                    .getLastPosition().z()));*/
            boolean isSneaking = player.isCrouching() && !isClimbing && isMoving;

            boolean isFlying = player.getPose() == Pose.FALL_FLYING || player.getAbilities().flying;

            a.getAction(SprintAction.actionName).ifPresent(sprint -> sprint.setActionState(isSprinting));
            a.getAction(ElytraFlightAction.actionName).ifPresent(elytra -> elytra.setActionState(isFlying));
            a.setLastPosition(player.position());

            a.getEnabledActions().forEach((actionName, action) -> action.tick(player, a));

        });
    }

    @Override
    public void onPlayerTickAfter(TickEvent.PlayerTickEvent playerTickEvent) {

    }
}
