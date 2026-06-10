package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * ParCool 3.x compatibility, safe to load without it: calls into the mod go through {@link ParcoolBridge}.
 * <p>
 * ParCool 3 decides its actions on the local client, posting its action events there, and replays start/finish on
 * the server when the client's state packet arrives. So AoS charges server side only (start cost on
 * {@code Start.Post}, a drain on {@code Tick.Post} while a continuous action is doing, the finish cost on
 * {@code Finish.Post}), and blocks on the client ({@code TryToStart}/{@code TryToContinue}) against its view of the
 * stamina.
 * <p>
 * ParCool 3 has no stamina type registry: the local player's stamina is picked by ParCool's client option
 * {@code used_stamina} (Default, Hunger or Elenai's Feathers). A mixin swaps ParCool's own (Default) stamina for
 * {@link ParcoolStamina}, which shows AoS's stamina to ParCool and swallows ParCool's own costs while this compat is
 * enabled, so the actions are charged once, by AoS. Hunger is kept: it charges food, not stamina.
 */
public final class ParcoolCompat {

    public static final String MOD_ID = "parcool";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    private ParcoolCompat() {
    }

    public static boolean isActive() {
        // ParCool asks for its stamina before a world (and so the server config) is loaded.
        return LOADED && AoSServerConfig.SPEC.isLoaded() && ParcoolConfig.ENABLED.get();
    }

    /** Common setup: hooks ParCool's action events. */
    public static void init() {
        if (LOADED) ParcoolBridge.register();
    }

    /** Client setup: hides ParCool's stamina HUD while AoS's stamina stands in for it. */
    public static void initClient() {
        if (LOADED) ParcoolClientBridge.register();
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
