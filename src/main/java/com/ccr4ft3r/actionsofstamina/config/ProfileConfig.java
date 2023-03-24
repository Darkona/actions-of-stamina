package com.ccr4ft3r.actionsofstamina.config;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;

import static java.lang.Integer.*;

public class ProfileConfig {

    private static final ForgeConfigSpec.Builder BUILDER_SLUGGISH = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder BUILDER_EXHAUSTED = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder BUILDER_BREATHLESS = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder BUILDER_CUSTOM = new ForgeConfigSpec.Builder();

    private static final Data PROFILE_SLUGGISH = new Data(BUILDER_SLUGGISH, AoSProfile.SLUGGISH);
    private static final Data PROFILE_EXHAUSTED = new Data(BUILDER_EXHAUSTED, AoSProfile.EXHAUSTED);
    private static final Data PROFILE_BREATHLESS = new Data(BUILDER_BREATHLESS, AoSProfile.BREATHLESS);
    private static final Data PROFILE_CUSTOM = new Data(BUILDER_CUSTOM, AoSProfile.CUSTOM);

    public static final ForgeConfigSpec CONFIG_SLUGGISH = BUILDER_SLUGGISH.build();
    public static final ForgeConfigSpec CONFIG_EXHAUSTED = BUILDER_EXHAUSTED.build();
    public static final ForgeConfigSpec CONFIG_BREATHLESS = BUILDER_BREATHLESS.build();
    public static final ForgeConfigSpec CONFIG_CUSTOM = BUILDER_CUSTOM.build();

    private static Data CURRENT_PROFILE;

    public static Data getProfile() {
        if (CURRENT_PROFILE == null)
            updateChoosedProfile();
        return CURRENT_PROFILE;
    }

    public static void updateChoosedProfile() {
        AoSProfile profile = MainConfig.CONFIG_DATA.profileToUse.get();
        switch (profile) {
            case CUSTOM -> CURRENT_PROFILE = PROFILE_CUSTOM;
            case SLUGGISH -> CURRENT_PROFILE = PROFILE_SLUGGISH;
            case EXHAUSTED -> CURRENT_PROFILE = PROFILE_EXHAUSTED;
            case BREATHLESS -> CURRENT_PROFILE = PROFILE_BREATHLESS;
        }
        LogUtils.getLogger().info("Stamina profile {} will be used for adding exhaustion.", profile);
    }

    public static class Data {

        public static final String MIN_FOR = "Defines how many remaining feathers (value 1 is equals to half a feather) the player must have to being able to ";
        public static final String COSTS = "Defines how many feathers (value 1 is equals to half a feather) the players will loose for";
        public static final String ENABLE_FOR = "Using stamina for ";
        public static final String AFTER_ACTION = "Decrease feathers bar value by the defined costs after ";
        public static final String AFTER_TIME = "Decrease feathers bar value by the defined costs after %s for X ticks";
        private final AoSProfile profile;
        private final ForgeConfigSpec.Builder builder;
        public ForgeConfigSpec.IntValue initialCosts;
        public ForgeConfigSpec.BooleanValue forSprinting;
        public ForgeConfigSpec.BooleanValue forJumping;
        public ForgeConfigSpec.BooleanValue forCrawling;
        public ForgeConfigSpec.BooleanValue forBlocking;
        public ForgeConfigSpec.BooleanValue forAttacking;
        public ForgeConfigSpec.BooleanValue forFlying;
        public ForgeConfigSpec.BooleanValue forParagliding;
        public ForgeConfigSpec.IntValue afterSprinting;
        public ForgeConfigSpec.IntValue afterJumping;
        public ForgeConfigSpec.IntValue afterCrawling;
        public ForgeConfigSpec.IntValue afterBlocking;
        public ForgeConfigSpec.IntValue afterAttacking;
        public ForgeConfigSpec.IntValue afterFlying;
        public ForgeConfigSpec.IntValue afterParagliding;
        public ForgeConfigSpec.IntValue costsForSprinting;
        public ForgeConfigSpec.IntValue costsForJumping;
        public ForgeConfigSpec.IntValue costsForCrawling;
        public ForgeConfigSpec.IntValue costsForBlocking;
        public ForgeConfigSpec.IntValue costsForAttacking;
        public ForgeConfigSpec.IntValue costsForFlying;
        public ForgeConfigSpec.IntValue costsForParagliding;
        public ForgeConfigSpec.IntValue minForSprinting;
        public ForgeConfigSpec.IntValue minForJumping;
        public ForgeConfigSpec.IntValue minForCrawling;
        public ForgeConfigSpec.IntValue minForBlocking;
        public ForgeConfigSpec.IntValue minForAttacking;
        public ForgeConfigSpec.IntValue minForParagliding;
        public ForgeConfigSpec.IntValue minForFlying;
        public ForgeConfigSpec.DoubleValue attackSpeedMultiplier;
        public ForgeConfigSpec.BooleanValue onlyForHits;

        public Data(ForgeConfigSpec.Builder builder, AoSProfile profile) {
            this.builder = builder;
            this.profile = profile;
            builder.comment("When setting these values, keep in mind that 20 ticks last one second (in the best case)."
                + "\n So if you want to drop the feathers bar by 1 (half a feather) after 2.5 seconds of sprinting, you have to specify 50 (2.5 seconds * 20 ticks/second = 50 ticks)."
                + "\n To slow down feathers regeneration you can edit the feathers-common.toml of the Feathers mod.");

            initialCosts = defineRange(COSTS + "starting an action", "initialCosts", 0, 6, 0, 1, 2);
            builder.push("Movement actions");
            forSprinting = define(ENABLE_FOR + "sprinting", "enableForSprinting", true, true, true);
            forJumping = define(ENABLE_FOR + "jumping", "enableForJumping", false, true, true);
            forCrawling = define(ENABLE_FOR + "crawling (for vanilla, GoProne & Personality Mod)", "enableForCrawling", true, true, true);
            forBlocking = define(ENABLE_FOR + "holding the shield", "enableForHoldingShield", true, true, true);
            forAttacking = define(ENABLE_FOR + "attacking", "enableForAttacking", false, true, true);
            forFlying = define(ENABLE_FOR + "flying (elytra and Golden ring)", "enableForFlying", true, true, true);
            onlyForHits = define(ENABLE_FOR + "attacking only when hitting entities", "onlyForHits", true, false, false);
            forParagliding = define(ENABLE_FOR + "paragliding", "enableForParagliding", true, true, true);

            costsForSprinting = defineRange(COSTS + "sprinting", "costsForSprinting", 1, 20, 1, 1, 1);
            costsForJumping = defineRange(COSTS + "jumping", "costsForJumping", 1, 20, 1, 1, 1);
            costsForCrawling = defineRange(COSTS + "crawling (for vanilla, GoProne & Personality Mod)", "costsForCrawling", 1, 20, 1, 1, 1);
            costsForBlocking = defineRange(COSTS + "holding the shield", "costsForHoldingShield", 1, 20, 1, 1, 1);
            costsForAttacking = defineRange(COSTS + "attacking", "costsForAttacking", 1, 20, 1, 1, 1);
            costsForFlying = defineRange(COSTS + "flying", "costsForFlying", 1, 20, 1, 1, 1);
            costsForParagliding = defineRange(COSTS + "paragliding", "costsForParagliding", 1, 20, 1, 1, 1);

            minForSprinting = defineRange(MIN_FOR + "sprint", "minimumForSprinting", 0, 20, 2, 3, 4);
            minForJumping = defineRange(MIN_FOR + "jump", "minimumForJumping", 0, 20, 0, 0, 1);
            minForCrawling = defineRange(MIN_FOR + "crawl (for vanilla, GoProne & Personality Mod)", "minimumForCrawling", 0, 20, 1, 2, 3);
            minForBlocking = defineRange(MIN_FOR + "holding the shield", "minimumForHoldingShield", 0, 20, 1, 2, 3);
            minForAttacking = defineRange(MIN_FOR + "attack", "minimumForAttacking", 0, 20, 2, 4, 6);
            minForFlying = defineRange(MIN_FOR + "fly", "minimumForFlying", 0, 20, 2, 3, 4);
            minForParagliding = defineRange(MIN_FOR + "paraglide", "minimumForParagliding", 0, 20, 2, 3, 4);

            afterSprinting = defineRange(AFTER_TIME.formatted("sprinting"), "afterSprinting", 1, 1200, 75, 50, 25);
            afterJumping = defineRange(AFTER_ACTION + "jumping X times", "afterJumping", 1, 10, 1, 1, 1);
            afterCrawling = defineRange(AFTER_TIME.formatted("crawling (for vanilla, GoProne & Personality Mod)"), "afterCrawling", 1, 1200, 65, 35, 20);
            afterBlocking = defineRange(AFTER_TIME.formatted("holding the shield"), "afterHoldingShield", 1, 1200, 45, 20, 12);
            afterAttacking = defineRange(AFTER_ACTION + "attacking X times", "afterAttacking", 1, 10, 3, 2, 1);
            afterFlying = defineRange(AFTER_TIME.formatted("flying"), "afterFlying", 1, 1200, 50, 30, 20);
            afterParagliding = defineRange(AFTER_TIME.formatted("paragliding"), "afterParargliding", 1, 1200, 50, 30, 20);

            attackSpeedMultiplier = defineRange("Determines how much additional stamina is spent when attacking entities with weapons depending on the attack speed", "attackSpeedMultiplier", 0d, 10d, 1.5d, 2d, 3d);
            builder.pop();
        }

        private ForgeConfigSpec.BooleanValue define(String comment, String property, boolean... profileValues) {
            return builder.comment(comment).define(property, get(profileValues));
        }

        private ForgeConfigSpec.IntValue defineRange(String comment, String property, Integer min, Integer max, Integer... profileValues) {
            return builder.comment(comment).defineInRange(property, get(profileValues), min, max);
        }

        private ForgeConfigSpec.DoubleValue defineRange(String comment, String property, Double min, Double max, Double... profileValues) {
            return builder.comment(comment).defineInRange(property, get(profileValues), min, max);
        }

        private ForgeConfigSpec.IntValue defineTime(String comment, String property, Integer... seconds) {
            return builder.comment(comment).defineInRange(property, get(seconds) * 20, 20, MAX_VALUE);
        }

        private boolean get(boolean... profileValues) {
            return profileValues.length <= profile.ordinal() ? profileValues[1] : profileValues[profile.ordinal()];
        }

        @SafeVarargs
        private <T> T get(T... profileValues) {
            return profileValues.length <= profile.ordinal() ? profileValues[1] : profileValues[profile.ordinal()];
        }
    }
}