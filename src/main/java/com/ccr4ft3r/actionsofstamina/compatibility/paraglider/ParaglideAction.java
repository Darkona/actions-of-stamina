package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;


public class ParaglideAction extends Action {

    public static final String actionName = "paragliding_action";

    public ParaglideAction() {
        super(ParagliderConfig.PARAGLIDING_COST.get(),
                ParagliderConfig.PARAGLIDING_MINIMUM_COST.get(),
                ParagliderConfig.PARAGLIDING_COOLDOWN.get(),
                ParagliderConfig.PARAGLIDING_FEATHERS_PER_SECOND.get(),
                ParagliderConfig.INHIBIT_REGEN_WHEN_PARAGLIDING.get(),
                0);
    }

    public ParaglideAction(CompoundTag tag) {
        super(tag);
    }
    @Override
    public String name() {
        return actionName;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {}

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {}

}
