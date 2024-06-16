package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import net.minecraftforge.common.ForgeConfigSpec;

public class ParagliderConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;



    static {
        BUILDER.comment("Paraglider settings").push("paraglider");
        BUILDER.pop();
        SPEC = BUILDER.build();


    }
}
