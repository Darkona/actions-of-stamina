package com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;



public class SprintAction extends Action {

    public static final String actionName = "sprint_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("sprint");


    public SprintAction() {
        super(SOURCE, AoSCommonConfig.SPRINT);
    }


    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return SPRINT;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player p, PlayerActions a) {
        p.setSprinting(false);
    }
}
