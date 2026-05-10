package com.ccr4ft3r.actionsofstamina.config;

import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpConfig;
import com.ccr4ft3r.actionsofstamina.stamina.BackendMode;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * {@code config/actionsofstamina-server.toml}: the stamina backend, AoS's internal stamina, vanilla actions and one
 * section per supported mod. A mod's section only does something when that mod is installed. Costs are in feathers
 * (defaults tuned for a 20-feather bar), delays in ticks.
 */
public final class AoSServerConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<BackendMode> BACKEND;
    public static final ModConfigSpec.BooleanValue ENABLE_DEBUGGING;

    public static final ModConfigSpec.BooleanValue INTERNAL_ENABLED;
    public static final ModConfigSpec.IntValue INTERNAL_MAX_FEATHERS;
    public static final ModConfigSpec.DoubleValue INTERNAL_REGEN_PER_SECOND;
    public static final ModConfigSpec.IntValue INTERNAL_REGEN_DELAY;
    public static final ModConfigSpec.DoubleValue INTERNAL_RECOVERY;

    public static final ActionCostConfig ATTACK;
    public static final ModConfigSpec.BooleanValue ALSO_FOR_NON_WEAPONS;
    public static final ModConfigSpec.BooleanValue ONLY_FOR_HITS;
    public static final ActionCostConfig JUMP;
    public static final ActionCostConfig SPRINT;
    public static final ActionCostConfig SWIM;
    public static final ActionCostConfig ELYTRA;
    public static final ActionCostConfig CRAWL;
    public static final ActionCostConfig SHIELD;

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder b = BUILDER;

        b.push("general");
        BACKEND = b.comment("Where stamina comes from:",
                        " AUTO     - Green Feathers when it is installed, otherwise AoS's internal stamina",
                        " FEATHERS - Green Feathers (falls back to the internal stamina if it isn't installed)",
                        " INTERNAL - AoS's internal stamina, even with Green Feathers installed")
                .defineEnum("backend", BackendMode.AUTO);
        ENABLE_DEBUGGING = b.comment("Log every action change and show the action debug HUD")
                .define("debugging", false);
        b.pop();

        b.comment("AoS's own stamina bar, used only when the backend is the internal one (no Green Feathers)").push("internal");
        INTERNAL_ENABLED = b.comment("Whether the internal stamina exists at all; if false, every action is free without Green Feathers")
                .define("enabled", true);
        INTERNAL_MAX_FEATHERS = b.comment("Size of the bar, in feathers")
                .defineInRange("max_feathers", 20, 1, 1000);
        INTERNAL_REGEN_PER_SECOND = b.comment("Feathers regenerated per second")
                .defineInRange("regen_per_second", 0.5, 0.0, 100.0);
        INTERNAL_REGEN_DELAY = b.comment("Minimum ticks without regeneration after any spend (actions may ask for longer)")
                .defineInRange("regen_delay", 30, 0, 1200);
        INTERNAL_RECOVERY = b.comment("After running out, the share of the bar to regain before acting again (0-1)")
                .defineInRange("exhaustion_recovery", 0.3, 0.0, 1.0);
        b.pop();

        b.comment("Vanilla actions").push("vanilla");
        ActionCostConfig.Builder attack = ActionCostConfig.builder(b, "attack", "Attacking", true)
                .cost(1.0, "Cost of an attack")
                .minStamina(1.0)
                .timesToCharge(3)
                .regenDelay(70);
        ALSO_FOR_NON_WEAPONS = attack.spec().comment("Whether attacks with non-weapons (bare hands, tools without attack damage) cost too")
                .define("also_for_non_weapons", false);
        ONLY_FOR_HITS = attack.spec().comment("Whether only attacks that hit an entity cost (misses stay free)")
                .define("only_for_hits", true);
        ATTACK = attack.build();
        JUMP = ActionCostConfig.builder(b, "jump", "Jumping", true)
                .cost(1.0, "Cost of a jump")
                .minStamina(1.0)
                .timesToCharge(4)
                .regenDelay(40)
                .build();
        SPRINT = ActionCostConfig.builder(b, "sprint", "Sprinting", true)
                .cost(0.0, "Cost to start")
                .minStamina(2.0)
                .perSecond(0.25)
                .regenDelay(40)
                .blocksRegen(true)
                .build();
        SWIM = ActionCostConfig.builder(b, "swim", "Swimming (the fast, sprint-swimming pose)", true)
                .cost(0.0, "Cost to start")
                .minStamina(2.0)
                .perSecond(0.5)
                .regenDelay(80)
                .blocksRegen(true)
                .build();
        ELYTRA = ActionCostConfig.builder(b, "elytra", "Elytra flying", true)
                .cost(0.0, "Cost to start")
                .minStamina(2.0)
                .perSecond(0.05)
                .regenDelay(20)
                .blocksRegen(true)
                .build();
        CRAWL = ActionCostConfig.builder(b, "crawl", "Crawling (moving in the swimming pose on land)", true)
                .cost(0.0, "Cost to start")
                .minStamina(1.0)
                .perSecond(0.1)
                .regenDelay(40)
                .blocksRegen(true)
                .build();
        SHIELD = ActionCostConfig.builder(b, "shield", "Holding up a shield", true)
                .cost(1.0, "Cost to raise it")
                .minStamina(0.0)
                .perSecond(0.2)
                .regenDelay(20)
                .blocksRegen(true)
                .build();
        b.pop();

        ParcoolConfig.init();
        ParagliderConfig.init();
        BetterCombatConfig.init();
        CombatRollConfig.init();
        EpicFightConfig.init();
        WallJumpConfig.init();
        GlidersConfig.init();

        SPEC = b.build();
    }

    private AoSServerConfig() {
    }
}
