package com.ccr4ft3r.actionsofstamina.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class AoSCommonConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ATTACKING_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Integer> ATTACKING_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> ATTACKING_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> ATTACKING_TIMES_PERFORMED_TO_EXHAUST;
    public static final ForgeConfigSpec.ConfigValue<Integer> ATTACKING_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALSO_FOR_NON_WEAPONS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ONLY_FOR_HITS;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SPRINTING_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INHIBIT_REGEN_WHEN_SPRINTING;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPRINTING_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPRINTING_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPRINTING_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> SPRINTING_FEATHERS_PER_SECOND;


    public static final ForgeConfigSpec.ConfigValue<Boolean> SWIMMING_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INHIBIT_REGEN_WHEN_SWIMMING;
    public static final ForgeConfigSpec.ConfigValue<Integer> SWIMMING_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> SWIMMING_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> SWIMMING_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> SWIMMING_FEATHERS_PER_SECOND;

    public static final ForgeConfigSpec.ConfigValue<Boolean> FLYING_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INHIBIT_REGEN_WHEN_FLYING;
    public static final ForgeConfigSpec.ConfigValue<Integer> FLYING_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> FLYING_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> FLYING_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> FLYING_FEATHERS_PER_SECOND;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CRAWLING_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INHIBIT_REGEN_WHEN_CRAWLING;
    public static final ForgeConfigSpec.ConfigValue<Integer> CRAWLING_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> CRAWLING_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> CRAWLING_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> CRAWLING_FEATHERS_PER_SECOND;

    public static final ForgeConfigSpec.ConfigValue<Boolean> JUMPING_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Integer> JUMPING_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> JUMPING_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> JUMPING_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> JUMPING_TIMES_PERFORMED_TO_EXHAUST;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HOLD_SHIELD_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOLD_SHIELD_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOLD_SHIELD_MINIMUM_COST;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOLD_SHIELD_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> HOLD_SHIELD_FEATHERS_PER_SECOND;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INHIBIT_REGEN_WHEN_HOLDING_SHIELD;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_DEBUGGING;

    static {

        BUILDER.push("Enabled Actions");

        ATTACKING_ENABLED = BUILDER
                .comment("Use feathers to attack.")
                .define("attack_enabled", true);

        SPRINTING_ENABLED = BUILDER
                .comment("Use feathers to sprint.")
                .define("sprint_enabled", true);

        SWIMMING_ENABLED = BUILDER
                .comment("Use feathers to swim.")
                .define("swim_enabled", true);

        FLYING_ENABLED = BUILDER
                .comment("Use feathers to fly.")
                .define("fly_enabled", true);

        CRAWLING_ENABLED = BUILDER
                .comment("Use feathers to crawl.")
                .define("crawl_enabled", true);

        JUMPING_ENABLED = BUILDER
                .comment("Use feathers to jump.")
                .define("jump_enabled", true);

        HOLD_SHIELD_ENABLED = BUILDER
                .comment("Use feathers to hold shield.")
                .define("shield_enabled", true);

        ONLY_FOR_HITS = BUILDER
                .comment("Whether to only consume feathers when hitting an entity")
                .define("only_for_hits", true);

        BUILDER.pop();

        BUILDER.push("Attacking");

        ATTACKING_COST = BUILDER
                .comment("How many feathers it cost to perform attacks")
                .defineInRange("attack_cost", 1, 0, 20);

        ATTACKING_MINIMUM_COST = BUILDER
                .comment("Minimum feathers available needed to perform attacks")
                .defineInRange("attack_min_cost", 1, 0, 20);

        ATTACKING_TIMES_PERFORMED_TO_EXHAUST = BUILDER
                .comment("How many times you can perform attacks before consuming feathers")
                .defineInRange("attack_times_performed_to_exhaust", 3, 1, 20);

        ALSO_FOR_NON_WEAPONS = BUILDER
                .comment("Whether to also consume feathers for non-weapon attacks")
                .define("also_for_non_weapons", false);

        ATTACKING_COOLDOWN = BUILDER
                .comment("How many ticks of cooldown to add before starting to regenerate feathers after attacking")
                .defineInRange("attack_cooldown", 70, 0, 100);

        BUILDER.pop();


        BUILDER.push("Sprinting");

        INHIBIT_REGEN_WHEN_SPRINTING = BUILDER
                .comment("Whether to inhibit feather regeneration when sprinting")
                .define("inhibit_regen_when_sprinting", true);

        SPRINTING_COST = BUILDER
                .comment("How many feathers it cost to start sprinting")
                .defineInRange("sprint_cost", 0, 0, 20);

        SPRINTING_MINIMUM_COST = BUILDER
                .comment("Minimum feathers available needed to start sprinting")
                .defineInRange("sprint_min_cost", 2, 0, 20);

        SPRINTING_FEATHERS_PER_SECOND = BUILDER
                .comment("How many feathers are consumed per second when sprinting")
                .defineInRange("feathers_per_second", 0.25d, 0, 20);

        SPRINTING_COOLDOWN = BUILDER
                .comment("How many ticks of cooldown to add before starting to regenerate feathers after sprinting")
                .defineInRange("sprint_cooldown", 40, 0, 100);

        BUILDER.pop();

        BUILDER.push("Swimming");

        INHIBIT_REGEN_WHEN_SWIMMING = BUILDER
                .comment("Whether to inhibit feather regeneration when swimming")
                .define("inhibit_regen_when_swimming", true);

        SWIMMING_COST = BUILDER
                .comment("How many feathers it cost to start swimming")
                .defineInRange("swim_cost", 0, 0, 20);

        SWIMMING_MINIMUM_COST = BUILDER
                .comment("Minimum feathers available needed to start swimming")
                .defineInRange("swim_min_cost", 2, 0, 20);

        SWIMMING_FEATHERS_PER_SECOND = BUILDER
                .comment("How many feathers are consumed per second when swimming")
                .defineInRange("swim_feathers_per_second", 0.5d, 0, 20);

        SWIMMING_COOLDOWN = BUILDER
                .comment("How many ticks of cooldown to add before starting to regenerate feathers after swimming")
                .defineInRange("swim_cooldown", 80, 0, 100);

        BUILDER.pop();

        BUILDER.push("Flying");

        INHIBIT_REGEN_WHEN_FLYING = BUILDER
                .comment("Whether to inhibit feather regeneration when flying")
                .define("inhibit_regen_when_flying", true);

        FLYING_COST = BUILDER
                .comment("How many feathers it cost to start flying")
                .defineInRange("fly_cost", 0, 0, 20);

        FLYING_MINIMUM_COST = BUILDER
                .comment("Minimum feathers available needed to start flying")
                .defineInRange("fly_min_cost", 2, 0, 20);

        FLYING_FEATHERS_PER_SECOND = BUILDER
                .comment("How many feathers are consumed per second when flying")
                .defineInRange("fly_feathers_per_second", 0.05d, 0, 20);

        FLYING_COOLDOWN = BUILDER
                .comment("How many ticks of cooldown to add before starting to regenerate feathers after flying")
                .defineInRange("fly_cooldown", 20, 0, 100);

        BUILDER.pop();

        BUILDER.push("Crawling");

        INHIBIT_REGEN_WHEN_CRAWLING = BUILDER
                .comment("Whether to inhibit feather regeneration when crawling")
                .define("inhibit_regen_when_crawling", true);

        CRAWLING_COST = BUILDER
                .comment("How many feathers it cost to start crawling")
                .defineInRange("crawl_cost", 0, 0, 20);

        CRAWLING_MINIMUM_COST = BUILDER
                .comment("Minimum feathers available needed to start crawling")
                .defineInRange("crawl_min_cost", 1, 0, 20);

        CRAWLING_FEATHERS_PER_SECOND = BUILDER
                .comment("How many feathers are consumed per second when crawling")
                .defineInRange("crawl_feathers_per_second", 0.1d, 0, 20);

        CRAWLING_COOLDOWN = BUILDER
                .comment("How many ticks of cooldown to add before starting to regenerate feathers after crawling")
                .defineInRange("crawl_cooldown", 40, 0, 100);

        BUILDER.pop();

        BUILDER.push("Jumping");

        JUMPING_COST = BUILDER
                .comment("How many feathers it cost to jump")
                .defineInRange("jump_cost", 1, 0, 20);

        JUMPING_MINIMUM_COST = BUILDER
                .comment("Minimum feathers available needed to jump")
                .defineInRange("jump_min_cost", 1, 0, 20);

        JUMPING_TIMES_PERFORMED_TO_EXHAUST = BUILDER
                .comment("How many times you can jump before consuming feathers")
                .defineInRange("jump_times_performed_to_exhaust", 4, 0, 20);

        JUMPING_COOLDOWN = BUILDER
                .comment("How many ticks of cooldown to add before starting to regenerate feathers after jumping")
                .defineInRange("jump_cooldown", 40, 0, 100);

        BUILDER.pop();

        BUILDER.push("Holding Shield");

        HOLD_SHIELD_COST = BUILDER
                .comment("How many feathers it cost to hold shield")
                .defineInRange("shield_cost", 1, 0, 20);

        HOLD_SHIELD_MINIMUM_COST = BUILDER
                .comment("Minimum feathers available needed to hold shield")
                .defineInRange("shield_min_cost", 0, 0, 20);

        HOLD_SHIELD_FEATHERS_PER_SECOND = BUILDER
                .comment("How many feathers are consumed per second when holding shield")
                .defineInRange("shield_feathers_per_second", 0.2d, 0, 20);

        HOLD_SHIELD_COOLDOWN = BUILDER
                .comment("How many ticks of cooldown to add before starting to regenerate feathers after holding shield")
                .defineInRange("shield_cooldown", 20, 0, 100);

        INHIBIT_REGEN_WHEN_HOLDING_SHIELD = BUILDER
                .comment("Whether to inhibit feather regeneration when holding shield")
                .define("inhibit_regen_when_holding_shield", true);

        BUILDER.pop();

        BUILDER.push("Debugging");

        ENABLE_DEBUGGING = BUILDER
                .comment("Enable debugging messages")
                .define("debugging", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
