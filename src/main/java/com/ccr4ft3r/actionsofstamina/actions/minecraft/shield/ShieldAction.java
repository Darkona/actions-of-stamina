package com.ccr4ft3r.actionsofstamina.actions.minecraft.shield;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ShieldAction extends Action {


    public static final String actionName = "shield_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("shield");


    public ShieldAction() {
        super(SOURCE, AoSCommonConfig.SHIELD);
    }


    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return SHIELD;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }


}
