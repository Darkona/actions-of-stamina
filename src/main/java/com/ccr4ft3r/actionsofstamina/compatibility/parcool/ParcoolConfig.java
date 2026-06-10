package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

/**
 * {@code [parcool]}: one subsection per ParCool action. Defaults are ParCool's own costs scaled to a 20-feather bar
 * (ParCool 3's default bar is 2000 points, so 100 points = 1 feather; its per-tick costs become per-second rates).
 * Names follow ParCool 4 where the move is the same (ParCool 3's ClingToCliff is {@code hang_on}, CatLeap {@code
 * long_jump}, Flipping {@code trick_jump}, Roll and Tap {@code breakfall}).
 * No ParCool classes here.
 */
public final class ParcoolConfig {

    public static final ForgeConfigSpec.BooleanValue ENABLED;

    /** A ParCool action by its id path (e.g. {@code fast_run}) with its costs. */
    public record Entry(String name, ActionCostConfig costs) {
    }

    private static final Entry[] ENTRIES;

    static {
        ForgeConfigSpec.Builder b = AoSServerConfig.BUILDER;
        b.comment("ParCool (parkour) actions. AoS charges them server side and blocks starting or continuing them",
                " when stamina runs short. While this is enabled, ParCool's own stamina (its client option used_stamina",
                " = Default) shows AoS's stamina and charges nothing, so each action is charged once, by AoS. A player",
                " who picks used_stamina = Hunger pays both: food to ParCool and stamina to AoS.").push("parcool");
        ENABLED = b.comment("Whether ParCool actions cost stamina (only when ParCool is installed)").define("enabled", true);
        ENTRIES = new Entry[]{
                continuous(b, "fast_run", 0, 0.4, 0),
                continuous(b, "fast_swim", 0, 0.6, 0),
                continuous(b, "horizontal_wall_run", 0, 0.4, 0),
                continuous(b, "hang_on", 0, 0.4, 0),
                continuous(b, "hang_down", 0, 0.6, 0),
                continuous(b, "pole_climb", 0, 0, 0),
                continuous(b, "slide_down", 0, 1.6, 0),
                continuous(b, "ride_zipline", 0, 0.4, 0),
                continuous(b, "crawl", 0, 0, 0),
                continuous(b, "slide", 0, 0, 0),
                continuous(b, "dive", 0, 0, 0),
                continuous(b, "skydive", 0, 0, 0),
                continuous(b, "hide_in_block", 0, 0, 0),
                continuous(b, "vault", 0.5, 0, 0),
                continuous(b, "climb_up", 1.5, 0, 0),
                continuous(b, "dodge", 0.8, 0, 0),
                continuous(b, "charge_jump", 1.0, 0, 0),
                instant(b, "jump_from_bar", 1.0),
                instant(b, "wall_jump", 1.2),
                instant(b, "wall_run", 1.5),
                instant(b, "long_jump", 2.0),
                instant(b, "trick_jump", 0.8),
                // Charged when the breakfall lands (as a roll or a tap); readying it needs the cost.
                instant(b, "breakfall", 1.0),
        };
        b.pop();
    }

    private ParcoolConfig() {
    }

    /** Called by {@link AoSServerConfig} to define this section in order. */
    public static void init() {
    }

    private static Entry continuous(ForgeConfigSpec.Builder b, String name, double start, double perSecond, double finish) {
        return new Entry(name, ActionCostConfig.builder(b, name, "ParCool " + name, true)
                .cost(start, "Cost to start")
                .perSecond(perSecond)
                .finishCost(finish)
                .regenDelay(20)
                .blocksRegen(true)
                .build());
    }

    private static Entry instant(ForgeConfigSpec.Builder b, String name, double cost) {
        return new Entry(name, ActionCostConfig.builder(b, name, "ParCool " + name, true)
                .cost(cost, "Cost of the action")
                .regenDelay(20)
                .build());
    }

    @Nullable
    static Entry byName(String name) {
        for (Entry entry : ENTRIES) {
            if (entry.name.equals(name)) return entry;
        }
        return null;
    }
}
