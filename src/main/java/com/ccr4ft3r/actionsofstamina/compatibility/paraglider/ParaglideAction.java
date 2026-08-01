package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;

public class ParaglideAction extends Action {

    public ParaglideAction(ActionType type) {
        super(type);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {}

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {}

}
