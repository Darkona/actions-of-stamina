package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;


public class ParaglideAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("paraglide");

    public ParaglideAction() {
        super(SOURCE, ParagliderConfig.PARAGLIDE);
    }

    @Override
    public int id() {
        return PARAGLIDE;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {}

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {}

}
