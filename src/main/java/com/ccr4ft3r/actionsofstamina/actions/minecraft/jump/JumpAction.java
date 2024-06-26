package com.ccr4ft3r.actionsofstamina.actions.minecraft.jump;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class JumpAction extends Action {

    public static final String actionName = "jump_action";

    public JumpAction() {
        super( // JumpAction constructor
                AoSCommonConfig.JUMPING_COST.get(),
                AoSCommonConfig.JUMPING_MINIMUM_COST.get(),
                AoSCommonConfig.JUMPING_COOLDOWN.get(),
                0,
                false,
                AoSCommonConfig.JUMPING_TIMES_PERFORMED_TO_EXHAUST.get()
        );
    }

    public JumpAction(CompoundTag tag) {
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
