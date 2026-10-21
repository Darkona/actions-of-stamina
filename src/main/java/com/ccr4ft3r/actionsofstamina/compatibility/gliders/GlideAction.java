package com.ccr4ft3r.actionsofstamina.compatibility.gliders;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class GlideAction extends Action {

    public GlideAction(ActionType type) {
        super(type);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {
    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player instanceof ServerPlayer serverPlayer) GlidersCompat.close(serverPlayer);
    }
}
