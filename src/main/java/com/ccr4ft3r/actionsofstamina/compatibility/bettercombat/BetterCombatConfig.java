package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** {@code [bettercombat]}: every Better Combat weapon swing costs stamina. No Better Combat classes here. */
public final class BetterCombatConfig {

    public static final ActionCostConfig SWING;
    public static final ModConfigSpec.DoubleValue TWO_HANDED_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue OFF_HAND_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue COMBO_FINISHER_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue BLOCK_WHEN_SHORT;

    static {
        ActionCostConfig.Builder swing = ActionCostConfig.builder(AoSCommonConfig.BUILDER, "bettercombat",
                        "Better Combat: weapon swings cost stamina (only when Better Combat is installed). Swings of weapons with"
                                + " Better Combat attributes replace the vanilla attack cost.", true)
                .cost(0.4, "Cost of one swing")
                .regenDelay(40);
        ModConfigSpec.Builder b = swing.spec();
        TWO_HANDED_MULTIPLIER = b.comment("Cost multiplier for two-handed weapons")
                .defineInRange("two_handed_multiplier", 1.5, 0.0, 10.0);
        OFF_HAND_MULTIPLIER = b.comment("Cost multiplier for off-hand swings while dual wielding")
                .defineInRange("off_hand_multiplier", 0.75, 0.0, 10.0);
        COMBO_FINISHER_MULTIPLIER = b.comment("Cost multiplier for the last swing of a weapon's combo")
                .defineInRange("combo_finisher_multiplier", 1.25, 0.0, 10.0);
        BLOCK_WHEN_SHORT = b.comment("Whether swings are stopped when the stamina can't pay for them (otherwise they are just free)")
                .define("block_when_short", true);
        SWING = swing.build();
    }

    private BetterCombatConfig() {
    }

    /** Called by {@link AoSCommonConfig} to define this section in order. */
    public static void init() {
    }
}
