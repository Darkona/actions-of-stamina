package com.ccr4ft3r.actionsofstamina.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** {@code config/actionsofstamina-client.toml}: the internal stamina HUD (not used with Feathers of Fatigue). */
public final class AoSClientConfig {

    public static final ModConfigSpec.BooleanValue SHOW_HUD;
    public static final ModConfigSpec.IntValue HUD_X_OFFSET;
    public static final ModConfigSpec.IntValue HUD_Y_OFFSET;

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.comment("The internal stamina bar, drawn above the food bar when Feathers of Fatigue isn't the backend").push("hud");
        SHOW_HUD = b.define("enabled", true);
        HUD_X_OFFSET = b.defineInRange("x_offset", 0, -1000, 1000);
        HUD_Y_OFFSET = b.defineInRange("y_offset", 0, -1000, 1000);
        b.pop();
        SPEC = b.build();
    }

    private AoSClientConfig() {
    }
}
