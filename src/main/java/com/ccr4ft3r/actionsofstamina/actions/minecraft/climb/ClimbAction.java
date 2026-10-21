package com.ccr4ft3r.actionsofstamina.actions.minecraft.climb;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;

public class ClimbAction extends Action {

    public ClimbAction(ActionType type) {
        super(type);
    }

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
