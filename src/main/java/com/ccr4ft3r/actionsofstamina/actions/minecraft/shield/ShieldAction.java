package com.ccr4ft3r.actionsofstamina.actions.minecraft.shield;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ToolActions;

/** Holding up a shield: any item that can block like one ({@code ToolActions.SHIELD_BLOCK}), modded shields included. */
public class ShieldAction extends Action {

    public ShieldAction(ActionType type) {
        super(type);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    /** Out of stamina with the shield up: it comes down, or blocking would go on for free. */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player.isUsingItem() && player.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) player.stopUsingItem();
    }

}
