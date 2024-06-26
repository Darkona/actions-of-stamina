package com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ElytraAction extends Action {

    public static final String actionName = "elytra_action";

    public ElytraAction() {
        super(AoSCommonConfig.FLYING_COST.get(),
                AoSCommonConfig.FLYING_MINIMUM_COST.get(),
                AoSCommonConfig.FLYING_COOLDOWN.get(),
                AoSCommonConfig.FLYING_FEATHERS_PER_SECOND.get(),
                AoSCommonConfig.INHIBIT_REGEN_WHEN_FLYING.get(),
                0);
    }

    public ElytraAction(CompoundTag tag) {
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

    }
}
