package com.ccr4ft3r.actionsofstamina.actions.minecraft.building;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/** Building: placing a block, charged once every few blocks. Never refused: without the stamina, the block is free. */
public class BuildAction extends Action {

    public static final String actionName = "build_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("build");

    public BuildAction() {
        super(SOURCE, AoSServerConfig.BUILD);
    }

    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return BUILD;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}
