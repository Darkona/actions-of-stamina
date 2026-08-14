package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;

/** Wall-Jump TXF's wall cling: drains while the local player holds on to a wall ({@code ActionFlags.WALL_CLINGING}). */
public class WallClingAction extends Action {

    public WallClingAction(ActionType type) {
        super(type);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {
    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
    }
}
