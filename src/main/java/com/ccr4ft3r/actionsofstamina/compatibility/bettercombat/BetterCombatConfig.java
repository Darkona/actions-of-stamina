package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/** {@code [bettercombat]}: every Better Combat weapon swing costs stamina. No Better Combat classes here. */
public final class BetterCombatConfig {

    public static final ActionCostConfig SWING;
    public static final ModConfigSpec.DoubleValue TWO_HANDED_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue OFF_HAND_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue COMBO_FINISHER_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue BLOCK_WHEN_SHORT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CATEGORY_MULTIPLIERS;

    private static final double MAX_MULTIPLIER = 10.0;
    /** {@link #CATEGORY_MULTIPLIERS} parsed (lower-case category to multiplier); replaced whole on every config load. */
    private static volatile Object2DoubleOpenHashMap<String> categories = categoryMap();

    static {
        ActionCostConfig.Builder swing = ActionCostConfig.builder(AoSServerConfig.BUILDER, "bettercombat",
                        "Better Combat: weapon swings cost stamina (only when Better Combat is installed). Swings of weapons with"
                                + " Better Combat attributes replace the vanilla attack cost.", true)
                .cost(0.4, "Cost of one swing")
                .regenDelay(40);
        ModConfigSpec.Builder b = swing.spec();
        TWO_HANDED_MULTIPLIER = b.comment("Cost multiplier for two-handed weapons")
                .defineInRange("two_handed_multiplier", 1.5, 0.0, 10.0);
        OFF_HAND_MULTIPLIER = b.comment("Cost multiplier for off-hand swings while dual wielding")
                .defineInRange("off_hand_multiplier", 0.75, 0.0, 10.0);
        COMBO_FINISHER_MULTIPLIER = b.comment("Cost multiplier for the last swing of a weapon's combo")
                .defineInRange("combo_finisher_multiplier", 1.25, 0.0, 10.0);
        BLOCK_WHEN_SHORT = b.comment("Whether swings are stopped when the stamina can't pay for them (otherwise they are just free)")
                .define("block_when_short", true);
        CATEGORY_MULTIPLIERS = b.comment("Cost multipliers by the weapon's Better Combat category (\"category=multiplier\", 0-10), on top of the others;"
                                + " categories not listed cost 1x. Better Combat's own categories: sword, claymore, dagger, axe, heavy_axe, double_axe,"
                                + " mace, hammer, spear, trident, glaive, halberd, scythe, sickle, katana, rapier, cutlass, twin_blade, claw, fist,"
                                + " lance, anchor, staff, battlestaff, wand, pickaxe, coral_blade, soul_knife")
                .defineListAllowEmpty("category_multipliers",
                        List.of("dagger=0.6", "fist=0.6", "claw=0.7", "sickle=0.7", "rapier=0.8", "spear=0.9", "axe=1.2",
                                "claymore=1.2", "double_axe=1.2", "heavy_axe=1.3", "hammer=1.3", "mace=1.3", "anchor=1.4"),
                        () -> "sword=1.0", BetterCombatConfig::isCategoryEntry);
        SWING = swing.build();
    }

    private BetterCombatConfig() {
    }

    /** Called by {@link AoSServerConfig} to define this section in order. */
    public static void init() {
    }

    /** The multiplier for a Better Combat category (1 when it isn't listed, or the weapon has none). */
    public static double categoryMultiplier(@Nullable String category) {
        return category == null ? 1.0 : categories.getDouble(category.toLowerCase(Locale.ROOT));
    }

    /** Re-reads {@link #CATEGORY_MULTIPLIERS}; on every load or reload of the server config. */
    static void reloadCategories() {
        Object2DoubleOpenHashMap<String> parsed = categoryMap();
        for (String entry : CATEGORY_MULTIPLIERS.get()) {
            int eq = entry.indexOf('=');
            parsed.put(entry.substring(0, eq).trim().toLowerCase(Locale.ROOT), Double.parseDouble(entry.substring(eq + 1).trim()));
        }
        categories = parsed;
    }

    private static Object2DoubleOpenHashMap<String> categoryMap() {
        Object2DoubleOpenHashMap<String> map = new Object2DoubleOpenHashMap<>();
        map.defaultReturnValue(1.0);
        return map;
    }

    private static boolean isCategoryEntry(Object o) {
        if (!(o instanceof String entry)) return false;
        int eq = entry.indexOf('=');
        if (eq <= 0 || entry.substring(0, eq).isBlank()) return false;
        try {
            double multiplier = Double.parseDouble(entry.substring(eq + 1).trim());
            return multiplier >= 0.0 && multiplier <= MAX_MULTIPLIER;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
