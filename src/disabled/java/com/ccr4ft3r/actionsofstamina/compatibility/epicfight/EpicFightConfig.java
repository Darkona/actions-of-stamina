package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * {@code [epicfight]}: Epic Fight skills of these categories that would spend Epic Fight stamina spend AoS stamina
 * instead. Other categories keep Epic Fight's own stamina. No Epic Fight classes here.
 */
public final class EpicFightConfig {

    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ActionCostConfig DODGE;
    public static final ActionCostConfig GUARD;
    public static final ActionCostConfig INNATE;
    public static final ActionCostConfig MOVER;
    public static final ActionCostConfig BASIC_ATTACK;

    static {
        ModConfigSpec.Builder b = AoSServerConfig.BUILDER;
        b.comment("Epic Fight skills (only when Epic Fight is installed). A skill that can't be paid for fails, as it would",
                " without Epic Fight stamina.").push("epicfight");
        ENABLED = b.comment("Whether Epic Fight skills cost AoS stamina").define("enabled", true);
        DODGE = skill(b, "dodge", "Dodge skills (step, roll)", 2.0);
        GUARD = skill(b, "guard", "Guard skills, charged per blocked hit", 1.0);
        INNATE = skill(b, "innate", "Weapon innate skills that use stamina", 3.0);
        MOVER = skill(b, "mover", "Mover skills (e.g. double jump, demolition leap)", 2.0);
        BASIC_ATTACK = skill(b, "basic_attack", "Each swing of Epic Fight's basic attack combo in its battle mode. While in battle mode"
                + " this replaces AoS's vanilla attack cost, so a swing is only charged once", 1.0);
        b.pop();
    }

    private EpicFightConfig() {
    }

    /** Called by {@link AoSServerConfig} to define this section in order. */
    public static void init() {
    }

    private static ActionCostConfig skill(ModConfigSpec.Builder b, String name, String description, double cost) {
        return ActionCostConfig.builder(b, name, description, true)
                .cost(cost, "Cost of one use")
                .regenDelay(30)
                .build();
    }
}
