package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * {@code [paragliders]}: paragliding drains AoS stamina and Paragliders' own stamina wheel is replaced by the AoS
 * backend. No Paragliders classes here.
 */
public final class ParagliderConfig {

    public static final ModConfigSpec.IntValue FEATHERS_PER_VESSEL;
    /** {@code enabled} turns the whole integration on; off, Paragliders keeps its own stamina wheel (and its vessels). */
    public static final ActionCostConfig PARAGLIDE;

    static {
        ActionCostConfig.Builder paraglide = ActionCostConfig.builder(AoSServerConfig.BUILDER, "paragliders",
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

    /** Called by {@link AoSServerConfig} to define this section in order. */
    public static void init() {
    }
}
