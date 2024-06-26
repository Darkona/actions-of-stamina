package com.ccr4ft3r.actionsofstamina.actions.minecraft.shield;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ShieldAction extends Action {


    public static final String actionName = "shield_action";


    public ShieldAction() {
        super(AoSCommonConfig.HOLD_SHIELD_COST.get(),
                AoSCommonConfig.HOLD_SHIELD_MINIMUM_COST.get(),
                AoSCommonConfig.HOLD_SHIELD_COOLDOWN.get(),
                AoSCommonConfig.HOLD_SHIELD_FEATHERS_PER_SECOND.get(),
                AoSCommonConfig.INHIBIT_REGEN_WHEN_HOLDING_SHIELD.get(),
                0);
    }

    public ShieldAction(CompoundTag tag) {super(tag);}

    @Override
    public String name() {
        return actionName;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }


}
