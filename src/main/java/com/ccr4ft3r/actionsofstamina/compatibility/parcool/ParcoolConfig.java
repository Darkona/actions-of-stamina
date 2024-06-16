package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import net.minecraftforge.common.ForgeConfigSpec;

public class ParcoolConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;



    static {
        BUILDER.comment("Parcool settings").push("parcool");
        BUILDER.pop();
        SPEC = BUILDER.build();


    }
}
