package com.ccr4ft3r.actionsofstamina.config;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.ExhaustedAttackMode;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpConfig;
import com.ccr4ft3r.actionsofstamina.stamina.BackendMode;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * {@code config/actionsofstamina-server.toml}: the stamina backend, AoS's internal stamina, vanilla actions and one
 * section per supported mod. A mod's section only does something when that mod is installed. Costs are in feathers
 * (defaults tuned for a 20-feather bar), delays in ticks.
 */
public final class AoSServerConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.EnumValue<BackendMode> BACKEND;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUGGING;

    public static final ForgeConfigSpec.BooleanValue INTERNAL_ENABLED;
    public static final ForgeConfigSpec.IntValue INTERNAL_MAX_FEATHERS;
    public static final ForgeConfigSpec.DoubleValue INTERNAL_REGEN_PER_SECOND;
    public static final ForgeConfigSpec.IntValue INTERNAL_REGEN_DELAY;
    public static final ForgeConfigSpec.DoubleValue INTERNAL_RECOVERY;

    public static final ActionCostConfig ATTACK;
    public static final ForgeConfigSpec.BooleanValue ALSO_FOR_NON_WEAPONS;
    public static final ForgeConfigSpec.BooleanValue ONLY_FOR_HITS;
    public static final ForgeConfigSpec.EnumValue<ExhaustedAttackMode> EXHAUSTED_MODE;
    public static final ForgeConfigSpec.DoubleValue WEAKEN_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue WEAKEN_SPEED;
    public static final ForgeConfigSpec.BooleanValue WEAKEN_NON_WEAPONS;
    public static final ActionCostConfig JUMP;
    public static final ActionCostConfig SPRINT;
    public static final ActionCostConfig SWIM;
    public static final ActionCostConfig ELYTRA;
    public static final ForgeConfigSpec.BooleanValue ROCKET_BOOST_COSTS;
    public static final ActionCostConfig CRAWL;
    public static final ActionCostConfig SHIELD;
    public static final ActionCostConfig DRAW;
    public static final ActionCostConfig THROW;
    public static final ActionCostConfig MINE;
    public static final ForgeConfigSpec.BooleanValue MINE_SCALE_WITH_HARDNESS;
    public static final ForgeConfigSpec.DoubleValue MINE_MAX_HARDNESS_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue MINE_BLOCK_WHEN_EXHAUSTED;
    public static final ForgeConfigSpec.DoubleValue MINE_EXHAUSTED_BREAK_SPEED;
    public static final ActionCostConfig BUILD;
    public static final ActionCostConfig CLIMB;
    public static final ActionCostConfig ROW;
    public static final ActionCostConfig RIPTIDE;
    public static final ActionCostConfig FISH;
    public static final ActionCostConfig TILL;

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder b = BUILDER;

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
                .timesPerformedToExhaust(3)
                .regenDelay(70);
        ALSO_FOR_NON_WEAPONS = attack.spec().comment("Whether attacks with non-weapons (bare hands, tools without attack damage) cost too")
                .define("also_for_non_weapons", false);
        ONLY_FOR_HITS = attack.spec().comment("Whether only attacks that hit an entity cost (misses stay free)")
                .define("only_for_hits", true);
        EXHAUSTED_MODE = attack.spec().comment("What an attack without the stamina for it does:",
                        " CANCEL - it doesn't happen (no swing, no hit)",
                        " WEAKEN - it lands, with less damage and a slower attack speed until the stamina is back")
                .defineEnum("exhausted_mode", ExhaustedAttackMode.CANCEL);
        WEAKEN_DAMAGE = attack.spec().comment("WEAKEN: share of the attack damage left while the stamina is short (0-1)")
                .defineInRange("weaken_damage", 0.5, 0.0, 1.0);
        WEAKEN_SPEED = attack.spec().comment("WEAKEN: share of the attack speed left while the stamina is short (0.05-1)")
                .defineInRange("weaken_speed", 0.5, 0.05, 1.0);
        WEAKEN_NON_WEAPONS = attack.spec().comment("Girl mode. WEAKEN: whether attacks with non-weapons (bare hands, tools without attack damage) are weakened too",
                        " while the stamina is short, even when also_for_non_weapons leaves them free (false: only attacks that cost are weakened)")
                .define("weaken_non_weapons", true);
        ATTACK = attack.build();
        JUMP = ActionCostConfig.builder(b, "jump", "Jumping", true)
                .cost(1.0, "Cost of a jump")
                .minStamina(1.0)
                .timesPerformedToExhaust(4)
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
        ActionCostConfig.Builder elytra = ActionCostConfig.builder(b, "elytra",
                        "Flying with wings from the item tag actionsofstamina:stamina_wings (the elytra; datapacks add other mods' wings)", true)
                .cost(0.0, "Cost to start")
                .minStamina(2.0)
                .perSecond(0.05)
                .regenDelay(20)
                .blocksRegen(true);
        ROCKET_BOOST_COSTS = elytra.spec().comment("Whether flight keeps draining while a firework rocket boosts it")
                .define("rocket_boost_costs", false);
        ELYTRA = elytra.build();
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
        DRAW = ActionCostConfig.builder(b, "draw", "Drawing a bow, loading a crossbow or aiming a trident (any item used with their animation)", true)
                .cost(0.5, "Cost to start drawing")
                .minStamina(1.0)
                .perSecond(0.5)
                .regenDelay(30)
                .blocksRegen(true)
                .build();
        THROW = ActionCostConfig.builder(b, "throw",
                        "Throwing an item from the item tag actionsofstamina:throwables (snowball, egg, ender pearl, splash and lingering potions, trident on release)", true)
                .cost(0.5, "Cost of a throw")
                .minStamina(0.5)
                .timesPerformedToExhaust(1)
                .regenDelay(30)
                .build();
        ActionCostConfig.Builder mine = ActionCostConfig.builder(b, "mine", "Mining: breaking a block (never cancelled)", false)
                .cost(0.1, "Cost of breaking a block (of hardness 1 with scale_with_hardness)")
                .minStamina(0.5)
                .timesPerformedToExhaust(4)
                .regenDelay(30);
        MINE_SCALE_WITH_HARDNESS = mine.spec().comment("Whether each block costs the cost times its hardness (dirt 0.5, stone 1.5, obsidian 50; instant blocks are free)")
                .define("scale_with_hardness", true);
        MINE_MAX_HARDNESS_MULTIPLIER = mine.spec().comment("scale_with_hardness: the most a single block's hardness multiplies the cost by")
                .defineInRange("max_hardness_multiplier", 10.0, 0.0, 100.0);
        MINE_BLOCK_WHEN_EXHAUSTED = mine.spec().comment("Whether mining gets slower while the player can't afford it (min_stamina); it is never cancelled")
                .define("block_when_exhausted", false);
        MINE_EXHAUSTED_BREAK_SPEED = mine.spec().comment("block_when_exhausted: share of the normal break speed left while the stamina is short (0.01-1)")
                .defineInRange("exhausted_break_speed", 0.3, 0.01, 1.0);
        MINE = mine.build();
        BUILD = ActionCostConfig.builder(b, "build", "Building: placing a block (never refused)", false)
                .cost(0.1, "Cost of placing a block")
                .minStamina(0.0)
                .timesPerformedToExhaust(4)
                .regenDelay(30)
                .build();
        CLIMB = ActionCostConfig.builder(b, "climb", "Climbing: going up a ladder, vines, scaffolding or anything else climbable (down is free)", false)
                .cost(0.0, "Cost to start")
                .minStamina(1.0)
                .perSecond(0.3)
                .regenDelay(30)
                .blocksRegen(true)
                .build();
        ROW = ActionCostConfig.builder(b, "row",
                        "Rowing a boat from the entity tag actionsofstamina:rowed_boats as its driver (vanilla boats and chest boats; datapacks add other mods' boats)", false)
                .cost(0.0, "Cost to start")
                .minStamina(1.0)
                .perSecond(0.15)
                .regenDelay(30)
                .blocksRegen(true)
                .build();
        RIPTIDE = ActionCostConfig.builder(b, "riptide", "Launching with a Riptide trident, charged on release", false)
                .cost(1.0, "Cost of a launch")
                .minStamina(1.0)
                .regenDelay(40)
                .build();
        FISH = ActionCostConfig.builder(b, "fish", "Fishing: casting a rod and reeling it in, each charged (any item with the fishing rod's cast ability)", false)
                .cost(0.25, "Cost of a cast or a reel")
                .minStamina(0.5)
                .regenDelay(20)
                .build();
        TILL = ActionCostConfig.builder(b, "till",
                        "Working a block with a tool: tilling with a hoe, making a path with a shovel, stripping logs and scraping or unwaxing copper with an axe (modded tools with those abilities too)", false)
                .cost(0.25, "Cost of working a block")
                .minStamina(0.5)
                .timesPerformedToExhaust(2)
                .regenDelay(30)
                .build();
        b.pop();

        ParcoolConfig.init();
        ParagliderConfig.init();
        BetterCombatConfig.init();
        CombatRollConfig.init();
        EpicFightConfig.init();
        WallJumpConfig.init();
        GlidersConfig.init();
        CreateConfig.init();

        SPEC = b.build();
    }

    private AoSServerConfig() {
    }
}
