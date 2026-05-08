package com.ccr4ft3r.actionsofstamina.compatibility.gliders;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;

/** {@code [gliders]}: holding on to a Gliders glider drains stamina. No Gliders classes here. */
public final class GlidersConfig {

    public static final ActionCostConfig GLIDE = ActionCostConfig.builder(AoSCommonConfig.BUILDER, "gliders",
                    "Gliders: gliding drains stamina (you hold on to the glider). A glider can't be deployed without the stamina"
                            + " to begin, and closes when the stamina runs out (only when Gliders is installed)", true)
            .cost(0.0, "Cost to deploy the glider")
            .minStamina(1.0)
            .perSecond(0.1)
            .regenDelay(20)
            .blocksRegen(true)
            .build();

    private GlidersConfig() {
    }

    /** Called by {@link AoSCommonConfig} to define this section in order. */
    public static void init() {
    }
}
