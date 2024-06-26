package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import net.minecraftforge.common.ForgeConfigSpec;

public class ParagliderConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> PARAGLIDING_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INHIBIT_REGEN_WHEN_PARAGLIDING;
    public static final ForgeConfigSpec.ConfigValue<Integer> PARAGLIDING_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> PARAGLIDING_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> PARAGLIDING_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> PARAGLIDING_FEATHERS_PER_SECOND;


    static {
        BUILDER.comment("Paraglider settings").push("paraglider");

        PARAGLIDING_ENABLED = BUILDER.comment("Enable paragliding")
                .define("paraglidingEnabled", true);

        INHIBIT_REGEN_WHEN_PARAGLIDING = BUILDER.comment("Inhibit regeneration when paragliding")
                .define("inhibitRegenWhenParagliding", true);

        PARAGLIDING_COST = BUILDER.comment("Cost of paragliding")
                .defineInRange("paraglidingCost", 0, 0, Integer.MAX_VALUE);

        PARAGLIDING_MINIMUM_COST = BUILDER.comment("Minimum cost of paragliding")
                .defineInRange("paraglidingMinimumCost", 1, 0, Integer.MAX_VALUE);

        PARAGLIDING_COOLDOWN = BUILDER.comment("Cooldown of paragliding")
                .defineInRange("paraglidingCooldown", 20, 0, Integer.MAX_VALUE);

        PARAGLIDING_FEATHERS_PER_SECOND = BUILDER.comment("Feathers per second of paragliding")
                .defineInRange("paraglidingFeathersPerSecond", 0.1, 0.0, Double.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();


    }
}
