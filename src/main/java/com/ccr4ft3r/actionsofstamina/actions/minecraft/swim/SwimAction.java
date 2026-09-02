package com.ccr4ft3r.actionsofstamina.actions.minecraft.swim;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;

public class SwimAction extends Action {

    public SwimAction(ActionType type) {
        super(type);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    /**
     * Out of stamina (to begin, or any more): the swim ends. Swimming is sprinting in water, and the server puts the
     * pose back from the sprint flag every tick, so the sprint is what has to stop; the pose is dropped at once.
     */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        player.setSprinting(false);
        player.setSwimming(false);
    }
}

