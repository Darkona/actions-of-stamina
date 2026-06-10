package com.ccr4ft3r.actionsofstamina.config;

import net.minecraftforge.common.ForgeConfigSpec;

/** {@code config/actionsofstamina-client.toml}: the internal stamina HUD (not used with Green Feathers). */
public final class AoSClientConfig {

    public static final ForgeConfigSpec.BooleanValue SHOW_HUD;
    public static final ForgeConfigSpec.IntValue HUD_X_OFFSET;
    public static final ForgeConfigSpec.IntValue HUD_Y_OFFSET;

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("The internal stamina bar, drawn above the food bar when Green Feathers isn't the backend").push("hud");
        SHOW_HUD = b.define("enabled", true);
        HUD_X_OFFSET = b.defineInRange("x_offset", 0, -1000, 1000);
        HUD_Y_OFFSET = b.defineInRange("y_offset", 0, -1000, 1000);
        b.pop();
        SPEC = b.build();
    }

    private AoSClientConfig() {
    }
}
