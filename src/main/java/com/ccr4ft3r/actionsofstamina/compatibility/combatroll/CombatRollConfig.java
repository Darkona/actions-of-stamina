package com.ccr4ft3r.actionsofstamina.compatibility.combatroll;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;

/** {@code [combat_roll]}: rolling costs stamina and can't start without it. No Combat Roll classes here. */
public final class CombatRollConfig {

    public static final ActionCostConfig ROLL = ActionCostConfig.builder(AoSServerConfig.BUILDER, "combat_roll",
                    "Combat Roll: each roll costs stamina (only when Combat Roll is installed)", true)
            .cost(1.5, "Cost of a roll")
            .regenDelay(30)
            .build();

    private CombatRollConfig() {
    }

    /** Called by {@link AoSServerConfig} to define this section in order. */
    public static void init() {
    }
}
