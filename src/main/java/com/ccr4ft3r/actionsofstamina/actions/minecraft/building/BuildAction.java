package com.ccr4ft3r.actionsofstamina.actions.minecraft.building;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;

/** Building: placing a block, charged once every few blocks. Never refused: without the stamina, the block is free. */
public class BuildAction extends Action {

    public BuildAction(ActionType type) {
        super(type);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}
