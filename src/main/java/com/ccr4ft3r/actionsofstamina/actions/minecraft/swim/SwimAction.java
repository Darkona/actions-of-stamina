package com.ccr4ft3r.actionsofstamina.actions.minecraft.swim;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SwimAction extends Action {

    public static final String actionName = "swim_action";

    public SwimAction() {
        super(AoSCommonConfig.SWIMMING_COST.get(),
                AoSCommonConfig.SWIMMING_MINIMUM_COST.get(),
                AoSCommonConfig.SWIMMING_COOLDOWN.get(),
                AoSCommonConfig.SWIMMING_FEATHERS_PER_SECOND.get(),
                AoSCommonConfig.INHIBIT_REGEN_WHEN_SWIMMING.get(),
                0
        );
    }

    public SwimAction(CompoundTag tag) {
        super(tag);
    }

    @Override
    public String name() {
        return actionName;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        player.setSwimming(false);
    }
}

