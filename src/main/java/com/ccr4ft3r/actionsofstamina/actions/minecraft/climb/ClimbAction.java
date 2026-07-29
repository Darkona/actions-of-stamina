package com.ccr4ft3r.actionsofstamina.actions.minecraft.climb;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;

/**
 * Going up something climbable (ladders, vines, scaffolding, twisting vines, modded ladders): whatever the game
 * itself lets the player climb. Going down or holding on is free. Out of stamina, the player can't go up: they hold
 * on while sneaking, and otherwise slide down at the ladder's slow falling speed ({@code LivingEntityMixin}).
 */
public class ClimbAction extends Action {

    public ClimbAction(ActionType type) {
        super(type);
    }

    /**
     * Whether the player may be lifted up the climbable they're on (the check runs only while they push against it or
     * jump). Remote players on a client are moved by their own client: never held back here.
     */
    public static boolean mayClimbUp(Player player) {
        return player.level().isClientSide() && !player.isLocalPlayer() || PlayerActions.canPerform(player, VanillaActions.CLIMB);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}
