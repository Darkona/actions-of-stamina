package com.ccr4ft3r.actionsofstamina.actions.minecraft.swim;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SwimAction extends Action {

    public static final String actionName = "swim_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("swim");

    public SwimAction() {
        super(SOURCE, AoSServerConfig.SWIM);
    }


    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return SWIM;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        player.setSwimming(false);
    }
}

