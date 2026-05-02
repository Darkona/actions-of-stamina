package com.ccr4ft3r.actionsofstamina.actions.minecraft.jump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class JumpAction extends Action {

    public static final String actionName = "jump_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("jump");

    public JumpAction() {
        super(SOURCE, AoSCommonConfig.JUMP);
    }


    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return JUMP;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }

}
