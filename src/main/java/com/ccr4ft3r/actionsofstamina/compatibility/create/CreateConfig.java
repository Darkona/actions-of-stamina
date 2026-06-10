package com.ccr4ft3r.actionsofstamina.compatibility.create;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;

/** {@code [create]}: Create's hand-powered kinetic sources. No Create classes here. */
public final class CreateConfig {

    public static final ActionCostConfig CRANK;
    public static final ForgeConfigSpec.BooleanValue VALVE_HANDLES;

    static {
        ForgeConfigSpec.Builder b = AoSServerConfig.BUILDER;
        b.comment("Create (only when it is installed)").push("create");
        ActionCostConfig.Builder crank = ActionCostConfig.builder(b, "crank",
                        "Turning a hand crank (holding the use key on it). Without the stamina it doesn't turn, and it stops when the stamina runs out", true)
                .cost(0.0, "Cost to start turning")
                .minStamina(1.0)
                .perSecond(0.25)
                .regenDelay(30)
                .blocksRegen(true);
        VALVE_HANDLES = crank.spec().comment("Whether turning a valve handle costs the same (each turn drains while the player works it)")
                .define("valve_handles", true);
        CRANK = crank.build();
        b.pop();
    }

    private CreateConfig() {
    }

    /** Called by {@link AoSServerConfig} to define this section in order. */
    public static void init() {
    }
}
