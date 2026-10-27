package com.ccr4ft3r.actionsofstamina.config.compat;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ParagliderConfig {

    public static final ModConfigSpec.IntValue FEATHERS_PER_VESSEL;
    public static final ActionCostConfig PARAGLIDE;

    static {
        ActionCostConfig.Builder paraglide = ActionCostConfig.builder(ActionsConfig.BUILDER, "paragliders",
                        "Paragliders: paragliding costs AoS stamina and Paragliders reads its stamina from AoS (only when Paragliders is installed)", true)
                .cost(0.0, "Cost to open the paraglider")
                .minStamina(1.0)
                .perSecond(0.1)
                .regenDelay(20)
                .blocksRegen(true);
        FEATHERS_PER_VESSEL = paraglide.spec().comment("Max feathers each Stamina Vessel adds (Feathers of Fatigue: a max_feathers modifier;"
                        + " the internal stamina: a larger bar). 0 turns vessels off. Applies on joining, respawning, or when the vessel count changes")
                .defineInRange("feathers_per_vessel", 2, 0, 100);
        PARAGLIDE = paraglide.build();
    }

    private ParagliderConfig() {
    }

    public static void init() {
    }
}
