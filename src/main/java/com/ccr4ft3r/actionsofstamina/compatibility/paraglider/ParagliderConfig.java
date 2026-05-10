package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;

/**
 * {@code [paragliders]}: paragliding drains AoS stamina and Paragliders' own stamina wheel is replaced by the AoS
 * backend. No Paragliders classes here.
 */
public final class ParagliderConfig {

    /** {@code enabled} turns the whole integration on; off, Paragliders keeps its own stamina wheel. */
    public static final ActionCostConfig PARAGLIDE = ActionCostConfig.builder(AoSServerConfig.BUILDER, "paragliders",
                    "Paragliders: paragliding costs AoS stamina and Paragliders reads its stamina from AoS (only when Paragliders is installed)", true)
            .cost(0.0, "Cost to open the paraglider")
            .minStamina(1.0)
            .perSecond(0.1)
            .regenDelay(20)
            .blocksRegen(true)
            .build();

    private ParagliderConfig() {
    }

    /** Called by {@link AoSServerConfig} to define this section in order. */
    public static void init() {
    }
}
