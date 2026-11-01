package com.ccr4ft3r.actionsofstamina.config.compat;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;

public final class CombatRollConfig {

    public static final ActionCostConfig ROLL = ActionCostConfig.builder(ActionsConfig.BUILDER, "combat_roll",
                    "Combat Roll: each roll costs stamina (only when Combat Roll is installed)", true)
            .cost(1.5, "Cost of a roll")
            .regenDelay(30)
            .build();

    private CombatRollConfig() {
    }

    public static void init() {
    }
}
