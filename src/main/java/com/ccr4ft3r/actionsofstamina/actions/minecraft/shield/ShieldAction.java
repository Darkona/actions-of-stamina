package com.ccr4ft3r.actionsofstamina.actions.minecraft.shield;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.ItemAbilities;

public class ShieldAction extends Action {

    public ShieldAction(ActionType type) {
        super(type);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player.isUsingItem() && player.getUseItem().canPerformAction(ItemAbilities.SHIELD_BLOCK)) player.stopUsingItem();
    }

}
