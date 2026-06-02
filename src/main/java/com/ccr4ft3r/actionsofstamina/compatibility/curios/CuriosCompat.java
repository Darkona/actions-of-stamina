package com.ccr4ft3r.actionsofstamina.compatibility.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Curios compatibility, safe to load without it: calls into the mod go through {@link CuriosBridge}.
 * <p>
 * Stamina wings may be worn in a curio slot (mods that give the elytra its own slot). Curios lookups allocate, so
 * whether a player wears them is kept in {@code PlayerActions}: refreshed on the server when a curio changes
 * (Curios only posts that there) and on joining a level, and on the client every {@link #CLIENT_REFRESH_INTERVAL} ticks.
 */
public final class CuriosCompat {

    public static final String MOD_ID = "curios";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);
    /** Ticks between the client's checks of the local player's curios. */
    public static final int CLIENT_REFRESH_INTERVAL = 20;

    private CuriosCompat() {
    }

    /** Common setup: listens for curio changes. */
    public static void init() {
        if (LOADED) CuriosBridge.register();
    }

    /** Whether the player wears stamina wings in a curio slot (a Curios lookup: not every tick). */
    public static boolean wearsStaminaWings(Player player) {
        return LOADED && CuriosBridge.wearsStaminaWings(player);
    }
}
