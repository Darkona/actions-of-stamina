package com.ccr4ft3r.actionsofstamina.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MainConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Data CONFIG_DATA = new Data(BUILDER);
    public static final ForgeConfigSpec CONFIG = BUILDER.build();

    public static class Data {

        public ForgeConfigSpec.BooleanValue enableExtendedLogging;
        public ForgeConfigSpec.EnumValue<AoSProfile> profileToUse;

        public Data(ForgeConfigSpec.Builder builder) {
            builder.push("1 - General");
            profileToUse = builder.comment("Sets the level of difficulty for the stamina system (profile files are located in config/actionsofstamina). " +
                                          "\nSet this to CUSTOM and edit the custom-profile.toml to adapt each aspect to your preferences or edit the predefined ones.")
                                  .defineEnum("profileToUse", AoSProfile.EXHAUSTED);
            enableExtendedLogging = builder.comment("Enables extended mod logging - only used for trouble shooting.")
                                           .define("enableExtendedLogging", false);
            builder.pop();
        }
    }
}
