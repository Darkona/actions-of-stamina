package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * ParCool 4.x compatibility, safe to load without it: calls into the mod go through {@link ParcoolBridge}.
 * <p>
 * ParCool fires its action events on the side that decides the action (usually the local client) and replays
 * start/finish on the server when the client's state packet arrives. So AoS charges server side only (start cost on
 * {@code Start.Post}, a drain on {@code Tick.Post} while a continuous action is doing, the finish cost on
 * {@code Finish.Post}), and blocks on whichever side decides ({@code TryToStart}/{@code TryToContinue}) against
 * that side's view of the stamina.
 * <p>
 * AoS also registers its own ParCool stamina type, {@link #STAMINA_TYPE}, which shows AoS's stamina to ParCool and
 * swallows ParCool's own costs, so the actions are charged once, by AoS. It is ParCool's default {@code stamina_type}
 * when AoS is installed, and while this compat is enabled it also stands in for ParCool's own {@code parcool:parcool}
 * (see {@link #effectiveStaminaType}).
 */
public final class ParcoolCompat {

    public static final String MOD_ID = "parcool";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);
    /** AoS's ParCool stamina type. */
    public static final ResourceLocation STAMINA_TYPE = ActionsOfStamina.id("stamina");
    /** ParCool's own stamina type (its default), which would charge ParCool's actions a second time. */
    public static final ResourceLocation PARCOOL_STAMINA_TYPE = new ResourceLocation(MOD_ID, "parcool");

    private ParcoolCompat() {
    }

    public static boolean isActive() {
        // ParCool may ask for its stamina type before a world (and so the server config) is loaded.
        return LOADED && AoSServerConfig.SPEC.isLoaded() && ParcoolConfig.ENABLED.get();
    }

    /**
     * From the mixin on ParCool's server config: the stamina type ParCool uses for {@code configured}. While this
     * compat is enabled, ParCool's own stamina ({@link #PARCOOL_STAMINA_TYPE}) would charge every action a second
     * time, so AoS's type replaces it; any other choice is kept.
     */
    public static ResourceLocation effectiveStaminaType(ResourceLocation configured) {
        return PARCOOL_STAMINA_TYPE.equals(configured) && AoSServerConfig.SPEC.isLoaded() && isActive() ? STAMINA_TYPE : configured;
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
