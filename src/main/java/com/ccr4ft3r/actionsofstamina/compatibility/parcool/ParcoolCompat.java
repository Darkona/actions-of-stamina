package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

/**
 * ParCool 4.x compatibility, safe to load without it: calls into the mod go through {@link ParcoolBridge}.
 * <p>
 * ParCool fires its action events on the side that decides the action (usually the local client) and replays
 * start/finish on the server when the client's state packet arrives. So AoS charges server side only (start cost on
 * {@code Start.Post}, a drain on {@code Tick.Post} while a continuous action is doing, the finish cost on
 * {@code Finish.Post}), and blocks on whichever side decides ({@code TryToStart}/{@code TryToContinue}) against
 * that side's view of the stamina.
 */
public final class ParcoolCompat {

    public static final String MOD_ID = "parcool";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    private ParcoolCompat() {
    }

    public static boolean isActive() {
        return LOADED && ParcoolConfig.ENABLED.getAsBoolean();
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
