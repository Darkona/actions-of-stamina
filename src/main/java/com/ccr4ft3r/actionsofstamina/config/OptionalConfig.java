package com.ccr4ft3r.actionsofstamina.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class OptionalConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Data CONFIG_DATA = new Data(BUILDER);
    public static final ForgeConfigSpec CONFIG = BUILDER.build();

    public static class Data {

        public ForgeConfigSpec.BooleanValue enableParagliderStaminaProvider;

        public Data(ForgeConfigSpec.Builder builder) {
            builder.push("5 - Optional");
            enableParagliderStaminaProvider = builder.comment("Enable Feathers stamina provider for Paraglider")
                                                     .define("enableParagliderStaminaProvider", true);
            builder.pop();
        }
    }
}