package com.ccr4ft3r.actionsofstamina.config.compat;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class WallJumpConfig {

    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ActionCostConfig WALL_JUMP;
    public static final ActionCostConfig DOUBLE_JUMP;
    public static final ActionCostConfig WALL_CLING;

    static {
        ModConfigSpec.Builder b = ActionsConfig.BUILDER;
        b.comment("Wall-Jump TXF (only when it is installed). A jump or a grip that can't be paid for doesn't happen,",
                " and a player clinging to a wall lets go when the stamina runs out.").push("walljump");
        ENABLED = b.comment("Whether Wall-Jump TXF's moves cost stamina").define("enabled", true);
        WALL_JUMP = ActionCostConfig.builder(b, "wall_jump", "Jumping off a wall", true)
                .cost(0.5, "Cost of a wall jump")
                .regenDelay(20)
                .build();
        DOUBLE_JUMP = ActionCostConfig.builder(b, "double_jump", "Jumping again in mid-air", true)
                .cost(1.5, "Cost of a double jump (on its own: it doesn't also pay for a normal jump)")
                .regenDelay(20)
                .build();
        WALL_CLING = ActionCostConfig.builder(b, "wall_cling", "Clinging to a wall, and sliding down it afterwards", true)
                .cost(0.0, "Cost to grab the wall")
                .minStamina(0.5)
                .perSecond(0.4)
                .regenDelay(20)
                .blocksRegen(true)
                .build();
        b.pop();
    }

    private WallJumpConfig() {
    }

    public static void init() {
    }
}
