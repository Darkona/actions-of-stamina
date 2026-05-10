package com.ccr4ft3r.actionsofstamina.actions.minecraft.shield;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;

public class ShieldAction extends Action {


    public static final String actionName = "shield_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("shield");


    public ShieldAction() {
        super(SOURCE, AoSServerConfig.SHIELD);
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

    /** Out of stamina with the shield up: it comes down, or blocking would go on for free. */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldItem) player.stopUsingItem();
    }


}
