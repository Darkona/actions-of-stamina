package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

/**
 * ParCool 3.x compatibility, safe to load without it: calls into the mod go through {@link ParcoolBridge}.
 * <p>
 * ParCool 3 decides its actions on the local client, posting its action events there, and replays start/finish on
 * the server when the client's state packet arrives. So AoS charges server side only (start cost on
 * {@code Start.Post}, a drain on {@code Tick.Post} while a continuous action is doing, the finish cost on
 * {@code Finish.Post}), and blocks on the client ({@code TryToStart}/{@code TryToContinue}) against its view of the
 * stamina.
 * <p>
 * ParCool 3 has no stamina type registry: the local player's stamina handler is picked by ParCool's client option
 * {@code used_stamina} (PARCOOL, HUNGER or NONE) or the server's {@code forced_stamina}. A mixin swaps ParCool's own
 * (PARCOOL) handler for {@link ParcoolStamina}, which shows AoS's stamina to ParCool, swallows ParCool's own costs and
 * hides ParCool's stamina HUD while this compat is enabled, so the actions are charged once, by AoS. HUNGER is kept: it
 * charges food, not stamina.
 */
public final class ParcoolCompat {

    public static final String MOD_ID = "parcool";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    /** One action type per ParCool action AoS charges, in {@link ParcoolConfig}'s order. */
    private static final ActionType[] TYPES;

    static {
        ParcoolConfig.Entry[] entries = ParcoolConfig.entries();
        TYPES = new ActionType[entries.length];
        for (int i = 0; i < entries.length; i++) {
            ActionCostConfig costs = entries[i].costs();
            // A ParCool action that costs nothing is left to ParCool: players don't get it, so it never pauses regeneration.
            TYPES[i] = ActionTypes.register(ActionsOfStamina.id("parcool/" + entries[i].name()), costs,
                    () -> isActive() && costs.enabled() && costs.costsAnything(), Action::new);
        }
    }

    private ParcoolCompat() {
    }

    /** Mod construction: registers the action types above (set when this class loads). */
    public static void registerActions() {
    }

    /** The action type of ParCool action {@code name} (its id path), or null when AoS doesn't charge it. */
    @Nullable
    static ActionType typeOf(String name) {
        ParcoolConfig.Entry[] entries = ParcoolConfig.entries();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].name().equals(name)) return TYPES[i];
        }
        return null;
    }

    public static boolean isActive() {
        // ParCool asks for its stamina before a world (and so the server config) is loaded.
        return LOADED && AoSServerConfig.SPEC.isLoaded() && ParcoolConfig.ENABLED.getAsBoolean();
    }

    /** Common setup: hooks ParCool's action events. */
    public static void init() {
        if (LOADED) ParcoolBridge.register();
    }

    /** Whether ParCool's own sprint (fast run) is running and charged by this compat, so AoS's sprint stays out. */
    public static boolean ownsSprint(Player player) {
        return isActive() && ParcoolBridge.isFastRunning(player);
    }

    /** Whether ParCool's fast swim is running and charged by this compat, so AoS's swim stays out. */
    public static boolean ownsSwim(Player player) {
        return isActive() && ParcoolBridge.isFastSwimming(player);
    }

    /** Whether ParCool's crawl is running (charged by this compat), so AoS's crawl stays out. */
    public static boolean ownsCrawl(Player player) {
        return isActive() && ParcoolBridge.isCrawling(player);
    }
}
