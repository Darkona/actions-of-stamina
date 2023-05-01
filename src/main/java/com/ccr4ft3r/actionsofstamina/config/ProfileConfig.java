package com.ccr4ft3r.actionsofstamina.config;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

import static com.ccr4ft3r.actionsofstamina.data.ServerData.*;
import static com.ccr4ft3r.actionsofstamina.util.ArrayUtil.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

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

    public static void stopIfExhausted(ServerPlayer player, AoSAction action, Runnable stopper) {
        if (shouldStop(player, action) && getPlayerData(player).is(action)) {
            stopper.run();
            getPlayerData(player).set(action, false, player);
        }
    }

    public static boolean shouldStop(AoSAction action) {
        return getProfile().enabledByAction.get(action).get()
            && !hasEnoughFeathers(getProfile().costsByAction.get(action), getProfile().minByAction.get(action));
    }

    public static boolean shouldStop(ServerPlayer player, AoSAction action) {
        return getProfile().enabledByAction.get(action).get()
            && (getPlayerData(player).is(action)
            || !hasEnoughFeathers(getProfile().costsByAction.get(action), getProfile().minByAction.get(action), player));
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

    @SuppressWarnings({"TextBlockMigration", "SameParameterValue"})
    public static class Data {

        public static final String MIN_FOR = "Defines how many remaining feathers (value 1 is equals to half a feather) the player must have for ";
        public static final String COSTS = "Defines how many feathers (value 1 is equals to half a feather) the players will loose for ";
        public static final String ENABLE_FOR = "Using stamina for ";
        public static final String AFTER = "Decrease feathers bar value by the defined costs after %s for X %s%s";
        public static final String STOPS_REGEN = "Defines whether %s%s should stop the regeneration of feathers";
        private final AoSProfile profile;

        private final ForgeConfigSpec.Builder builder;

        public ForgeConfigSpec.DoubleValue attackSpeedMultiplier;
        public ForgeConfigSpec.BooleanValue onlyForHits;

        public Map<AoSAction, ForgeConfigSpec.IntValue> initialCostsByAction = Maps.newConcurrentMap();
        public Map<AoSAction, ForgeConfigSpec.BooleanValue> enabledByAction = Maps.newConcurrentMap();
        public Map<AoSAction, ForgeConfigSpec.IntValue> costsByAction = Maps.newConcurrentMap();
        public Map<AoSAction, ForgeConfigSpec.IntValue> minByAction = Maps.newConcurrentMap();
        public Map<AoSAction, ForgeConfigSpec.BooleanValue> regenerationByAction = Maps.newConcurrentMap();
        public Map<AoSAction, ForgeConfigSpec.IntValue> delayByAction = Maps.newConcurrentMap();

        public Data(ForgeConfigSpec.Builder builder, AoSProfile profile) {
            this.builder = builder;
            this.profile = profile;
            builder.comment("When setting these values, keep in mind that 20 ticks last one second (in the best case)."
                + "\n So if you want to drop the feathers bar by 1 (half a feather) after 2.5 seconds of sprinting, you have to specify 50 (2.5 seconds * 20 ticks/second = 50 ticks)."
                + "\n To slow down feathers regeneration you can edit the feathers-common.toml of the Feathers mod.");

            builder.push("1 - General");
            onlyForHits = define(ENABLE_FOR + "attacking only when hitting entities", "onlyForHits", true, false, false);
            attackSpeedMultiplier = defineRange("Determines how much additional stamina is spent when attacking entities with weapons depending on the attack speed", "attackSpeedMultiplier", 0d, 10d, 1.5d, 2d, 3d);
            builder.pop();

            for (AoSAction action : AoSAction.values()) {
                builder.push(action.getText(false));
                enabledByAction.put(action, define(ENABLE_FOR + action.getText(true)
                        + action.getDescription(), "enableFor" + action.getText(false),
                    action.isEnabled(profile)));
                costsByAction.put(action, defineRange(COSTS + action.getText(true)
                        + action.getDescription(), "costsFor" + action.getText(false),
                    1, 20, action.getCosts(profile)));
                minByAction.put(action, defineRange(MIN_FOR + action.getText(true) + action.getDescription(),
                    "minimumFor" + action.getText(false), 0, 20, action.getMin(profile)));

                if (action.getType() == ActionType.TICKS) {
                    initialCostsByAction.put(action, defineRange(COSTS + "starting  " + action.getText(true)
                            + action.getDescription(), "initialCostsFor" + action.getText(false),
                        0, 10, 0, 1, 2));
                    regenerationByAction.put(action, define(STOPS_REGEN.formatted(action.getText(true), action.getDescription()),
                        "stopRegenerationWhile" + action.getText(false), true));
                }

                ForgeConfigSpec.IntValue delayConfig;
                if (action.getType() == ActionType.TICKS) {
                    delayConfig = defineRange(AFTER.formatted(action.getText(true), "ticks",
                            action.getDescription()), "ExhaustAfter" + action.getText(false) + "For",
                        1, 1200, action.getDelay(profile));
                } else {
                    delayConfig = defineRange(AFTER.formatted(action.getText(true), "times",
                            action.getDescription()), "ExhaustAfter" + action.getText(false) + "For",
                        1, 10, action.getDelay(profile));
                }
                delayByAction.put(action, delayConfig);
                builder.pop();
            }
        }

        private ForgeConfigSpec.BooleanValue define(String comment, String property, Boolean... profileValues) {
            return builder.comment(comment).define(property, get(profile, profileValues).booleanValue());
        }

        private ForgeConfigSpec.IntValue defineRange(String comment, String property, Integer min, Integer max, Integer... profileValues) {
            return builder.comment(comment).defineInRange(property, get(profile, profileValues), min, max);
        }

        private ForgeConfigSpec.DoubleValue defineRange(String comment, String property, Double min, Double max, Double... profileValues) {
            return builder.comment(comment).defineInRange(property, get(profile, profileValues), min, max);
        }
    }
}